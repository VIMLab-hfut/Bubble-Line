package cn.hfut.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenerateDate {
    public static void main(String[] args) {
        Random rd = new Random();
        int initv = 15000;
        List<Integer> result = new ArrayList<>();
        List<Integer> result2 = new ArrayList<>();
        for (int  year = 2012; year <= 2021; year++) {
            for (int month = 1; month <=12; month++) {
                //System.out.print(year+"/"+month+"\', \'");
                initv += rd.nextInt(200)-50;
                result.add(initv);
                result2.add(2+rd.nextInt(15));
                //System.out.println(rd.nextInt(250)-50);
            }
        }
        System.out.println(result2);
    }
}
