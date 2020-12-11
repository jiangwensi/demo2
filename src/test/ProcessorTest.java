package test;

import com.company.domain.Transaction;
import com.company.domain.TxnType;
import com.company.service.Processor;
import com.company.service.Utility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ProcessorTest {

    Processor processor;
    Utility utility;
    ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        utility = mock(Utility.class);
        processor = new Processor();

        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void process() throws IOException {

        given(utility.month(2)).willReturn("Feb");

        processor.process("src/test/resources/data.txt", "src/test/resources/output.txt");

        List<String> readData = readFile("src/test/resources/output.txt");
        assertEquals(4, readData.size());
        Assertions.assertTrue(readData.get(0).startsWith("Total Income :"));
        assertTrue(readData.get(0).endsWith("21201.4"));
        assertTrue(readData.get(1).startsWith("Total Expenses :"));
        assertTrue(readData.get(1).endsWith("16042.99"));
        assertTrue(readData.get(2).startsWith("Total Savings :"));
        assertTrue(readData.get(2).endsWith("5158.41"));
        assertTrue(readData.get(3).startsWith("Top Expenses Month :"));
        assertTrue(readData.get(3).endsWith("6000.8 @Feb"));
    }

    @Test
    void readFile() throws IOException {
        List<String> results = testData();
        assertEquals(results, processor.readFile("src/test/resources/data.txt"));
    }

    @Test
    void readFileFileNotFoundException() {

        String path = "notexists.txt";
        processor.readFile(path);
        assertEquals("Invalid path "+path+"\r\n",outContent.toString());
    }

    @Test
    void readTransactions() {
        List<String> text = new ArrayList<>();
        text.add("25-01-2020,-4000,SBI MF");
        text.add("20-02-2020,1200.8,Royalty");
        text.add("17-03-2020,0,Stationary");

        Transaction txn0 = getTransaction("25-01-2020", "4000", TxnType.EXPENSE, "SBI MF");
        Transaction txn1 = getTransaction("20-02-2020", "1200.8", TxnType.INCOME, "Royalty");
        Transaction txn2 = getTransaction("17-03-2020", "0", TxnType.INCOME, "Stationary");

        List<Transaction> transactions = processor.readTransactions(text);

        assertEquals(text.size(), transactions.size());

        assertEquals(txn0, transactions.get(0));
        assertEquals(txn1, transactions.get(1));
        assertEquals(txn2, transactions.get(2));

    }


    @Test
    void generateReport() throws IOException {
        Map<String, String> resultMap = new LinkedHashMap<>();
        resultMap.put("Total Income", "21201.4");
        resultMap.put("Total Expenses", "16042.99");
        resultMap.put("Total Savings", "5158.41");
        resultMap.put("Top Expenses Month", "6000.8 @Feb");

        processor.generateReport("src/test/resources/output.txt", resultMap);
        List<String> readData = readFile("src/test/resources/output.txt");
        assertEquals(4, readData.size());
        assertTrue(readData.get(0).startsWith("Total Income :"));
        assertTrue(readData.get(0).endsWith("21201.4"));
        assertTrue(readData.get(1).startsWith("Total Expenses :"));
        assertTrue(readData.get(1).endsWith("16042.99"));
        assertTrue(readData.get(2).startsWith("Total Savings :"));
        assertTrue(readData.get(2).endsWith("5158.41"));
        assertTrue(readData.get(3).startsWith("Top Expenses Month :"));
        assertTrue(readData.get(3).endsWith("6000.8 @Feb"));
    }

    @Test
    void generateReportException() throws IOException {
        Map<String, String> resultMap = new LinkedHashMap<>();
        resultMap.put("Total Income", "21201.4");
        resultMap.put("Total Expenses", "16042.99");
        resultMap.put("Total Savings", "5158.41");
        resultMap.put("Top Expenses Month", "6000.8 @Feb");

        String path = "src/test/resources/invalidfolder";
        processor.generateReport(path, resultMap);
        assertEquals("Error while writing to file "+path+"\r\n",outContent.toString());
    }

    private List<String> testData() {
        List<String> results = new ArrayList<>();
        results.add("25-01-2020,-4000,SBI MF");
        results.add("28-01-2020,-1740.6,Stationary");
        results.add("25-02-2020,-2800.3,Grocery");
        results.add("28-02-2020,-3200.5,Swiggy");
        results.add("20-02-2020,1200.8,Royalty");
        results.add("17-03-2020,-200,Stationary");
        results.add("15-03-2020,-2600.60,Grocery");
        results.add("10-03-2020,-1500.99,Grocery");
        results.add("05-03-2020,20000.6,Salary");
        return results;
    }

    private Transaction getTransaction(String date, String value, TxnType txnType, String description) {
        return new Transaction(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                new BigDecimal(value), txnType, description);
    }


    private List<String> readFile(String path) throws IOException {
        List<String> results = new ArrayList<>();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            while ((line = reader.readLine()) != null) {
                results.add(line);
            }
        }
        return results;
    }
}