package com.fullsail.cerberus.helpers;

import java.util.ArrayList;
import java.util.List;

public class Encryption {

    ArrayList<Integer> keyStream;
    String letter;

    public static final ArrayList<String> ALPHABET= new ArrayList(List.of("_",".",",","?","!","@","a","b","c","d","e",
            "f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
            "0","1","2","3","4","5","6","7","8","9"));

    public Encryption(ArrayList<Integer> arrayList, String letter) {
        this.keyStream = arrayList;
        this.letter = letter;
    }

    @Override
    public String toString() {
        int tmp=0;

        for (int x=0; x<ALPHABET.size();x++){
            if(letter.equalsIgnoreCase(ALPHABET.get(x)))
                tmp=x;
        }

        if(keyStream.get(0)<0)
            tmp=tmp+27;
        else
            tmp=tmp+keyStream.get(0);

        while(tmp>ALPHABET.size()-1)
            tmp=tmp-ALPHABET.size();



        return ALPHABET.get(tmp);
    }
}
