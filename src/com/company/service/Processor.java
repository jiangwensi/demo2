package com.company.service;

import com.company.domain.Transaction;
import com.company.domain.TxnType;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

/**
 * Created by Jiang Wensi on 9/12/2020
 */
public class Processor {

    Utility utility;

    public Processor() {
        this.utility = new Utility();
    }

    public void process(String inputFilePath, String outputFilePath) {

        List<String> txnLines = this.readFile(inputFilePath);

        List<Transaction> txns = readTransactions(txnLines);

        BigDecimal expense = new BigDecimal(0);
        BigDecimal income = new BigDecimal(0);

        Map<Integer, BigDecimal> monthExpenses = new LinkedHashMap<>();

        Map<TxnType,List<Transaction>> txnTypeMap = txns.stream().collect(groupingBy(Transaction::getType));

        List<Transaction> expenseList = txnTypeMap.get(TxnType.EXPENSE);
        expense = expenseList.stream().map(Transaction::getValue).reduce(expense,(a,b)->a.add(b));

        List<Transaction> incomeList = txnTypeMap.get(TxnType.INCOME);
        income = incomeList.stream().map(Transaction::getValue).reduce(income,(a,b)->a.add(b));

        expenseList.forEach(e-> {
            int monthValue = e.getDate().getMonthValue();
            if (monthExpenses.get(monthValue) == null) {
                monthExpenses.put(monthValue, new BigDecimal(0));
            }
            monthExpenses.put(monthValue, monthExpenses.get(monthValue).add(e.getValue()));

        });

        BigDecimal saving = income.subtract(expense);

        int maxExpenseMonthValue = 0;
        BigDecimal maxMonthlyExpense = BigDecimal.ZERO;

        for (Map.Entry<Integer, BigDecimal> entry : monthExpenses.entrySet()) {
            if (entry.getValue().compareTo(maxMonthlyExpense) > 0) {
                maxMonthlyExpense = entry.getValue();
                maxExpenseMonthValue = entry.getKey();
            }
        }

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);

        Map<String, String> result = new LinkedHashMap<>();

        result.put("Total Income", df.format(income));
        result.put("Total Expenses", df.format(expense));
        result.put("Total Savings", df.format(saving));
        result.put("Top Expenses Month", df.format(maxMonthlyExpense) + " @" + utility.month(maxExpenseMonthValue));

        generateReport(outputFilePath, result);
    }

    public List<String> readFile(String path) {
        List<String> results = new ArrayList<>();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            while ((line = reader.readLine()) != null) {
                results.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Invalid path "+path);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while reading file "+path);
        }
        return results;
    }

    public List<Transaction> readTransactions(List<String> text) {
        List<Transaction> transactions = new ArrayList<>();
        if (text != null) {

            text.forEach(e-> {
                String[] txn = e.split(",");
                LocalDate date = LocalDate.parse(txn[0], DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                BigDecimal value = new BigDecimal(txn[1]);
                TxnType type;
                if (value.compareTo(BigDecimal.ZERO) < 0) {
                    type = TxnType.EXPENSE;
                } else {
                    type = TxnType.INCOME;
                }
                transactions.add(new Transaction(date, value.abs(), type, txn[2]));
            });

        }
        return transactions;
    }

    public void generateReport(String path, Map<String, String> resultMap) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                writer.write(entry.getKey() + " : " + entry.getValue());
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while writing to file "+path);
        }
    }

}
