package sample.controller;

import javafx.animation.RotateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by uran on 17-05-02.
 */
public class VariablatPublike {

    static int uid = 0;
    static int uid2 = 0;
    static String uemri = "";

    public static String server = "localhost";
    public static String emriShitores;
    public static String styleSheet;

    public static Image spinning = new Image("/sample/photo/spinner.png");
    public static Image doneSpinning = new Image("/sample/photo/doneSpinning.png");

    public static boolean leapYear(int viti) {
        return viti%4==0?(viti%200==0?false:viti%400==0?true:false):false;
//        if (viti % 4 == 0) {
//            if (viti % 200 == 0)
//                return false;
//            else if (viti % 400 == 0)
//                return true;
//            return true;
//        }
//        return false;
    }
    public static int ditetMuajit(boolean leap, int muaji) {
        int ditet = (muaji%2==1?(muaji>=7?30:31) : (muaji>=8?31:30));
        if (muaji==2 && leap)
            ditet = 29;
        else if (muaji==2 & !leap)
            ditet = 28;

        return ditet;
    }
    public static int javetNeMuaj (int ditet){
        return ditet / 7 + (ditet % 7 > 0 ? 1 : 0);
    }

    static double muaj, jave, dite, tvsh;

    static Map<Integer, String> dataMap = new HashMap<>();
    static Map<String, Integer> dep = new HashMap<>();
    static Map<Integer, String> revDep = new HashMap<>();
    static Map<String, Integer> revProdKat = new HashMap<>();
    static Map<String, Integer> revKons = new HashMap<>();
    static Map<Integer, String> mProdKat = new HashMap<>();

    static Set<String> prodKat = new HashSet<>();
    static Set<String> konsEmri = new HashSet<>();

    static double hyratM=0, hyratJ=0, hyratS=0, mes;
    static int pnt, pntA, pntP, kons, shitje, p, ps;

    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00€");
    static DecimalFormat decimal = new DecimalFormat("###,###,###.#");
    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    static SimpleDateFormat hms = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    static StringConverter converter = new StringConverter<LocalDate>() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        @Override
        public String toString(LocalDate date) {
            if (date != null) {
                return formatter.format(date);
            } else
                return "";
        }

        @Override
        public LocalDate fromString(String string) {
            if (string != null && !string.equals("")) {
                return LocalDate.parse(string, formatter);
            } else
                return null;
        }
    };

    static boolean shitjeBool, konsumatoret, punetoret, shtepi, raportet, produktet, settings;


    public static RotateTransition startSpinning(ImageView iv){
        RotateTransition transition = new RotateTransition(Duration.millis(700), iv);
        transition.setByAngle(360);
        transition.setCycleCount(-1);
        return transition;
    }

    public static void stopSpinning (RotateTransition transition, ImageView iv) {
        transition.stop();
        iv.setImage(doneSpinning);
        iv.setRotate(0);
    }


}
