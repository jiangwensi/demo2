package test;


import com.company.service.Utility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    Utility utility;

    @BeforeEach
    void setUp() {
        utility = new Utility();
    }

    @Test
    void month() {
        assertEquals("Jan",utility.month(1));
        assertEquals("Feb",utility.month(2));
        assertEquals("Mar",utility.month(3));
        assertEquals("Apr",utility.month(4));
        assertEquals("May",utility.month(5));
        assertEquals("Jun",utility.month(6));
        assertEquals("Jul",utility.month(7));
        assertEquals("Aug",utility.month(8));
        assertEquals("Sep",utility.month(9));
        assertEquals("Oct",utility.month(10));
        assertEquals("Nov",utility.month(11));
        assertEquals("Dec",utility.month(12));
        assertEquals("",utility.month(13));
    }
}