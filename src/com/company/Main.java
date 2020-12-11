package com.company;

import com.company.service.Processor;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter input file path:");
        String inputPath = scanner.nextLine();
        System.out.println("Please enter output file path:");
        String outputPath = scanner.nextLine();

        new Processor().process(inputPath,outputPath);

    }
}
