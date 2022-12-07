package com.fullsail.cerberus.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Mutator {

    ArrayList<Integer> keystream;
    ArrayList<Integer> right,left, middle;

    int j1Index,j2Index;

    public Mutator(ArrayList<Integer> keystream) {
        this.keystream = keystream;

        left=new ArrayList<>();
        right=new ArrayList<>();
        middle=new ArrayList<>();
    }

    private void firstCut(){

        ArrayList<Integer> right=new ArrayList<>();

        for(int x=0; x<keystream.size(); x=0) {
            if(keystream.get(x)>0){
                right.add(keystream.get(x));
                keystream.remove(x);
            }
            else{
                middle.add(keystream.get(0));
                keystream.remove(0);

                for(int y=0; y<keystream.size(); y=0){
                    if(keystream.get(y)>0) {
                        middle.add(keystream.get(y));
                        keystream.remove(y);
                    }
                    else{
                        middle.add(keystream.get(0));
                        keystream.remove(0);

                        left=keystream;

                        keystream=new ArrayList<>();

                        keystream.addAll(left);
                        keystream.addAll(middle);
                        keystream.addAll(right);

                        refresh();

                        return;
                    }
                }
            }
        }

    }

    private void secondCut() {

        int count;

        if(keystream.get(0)<1)
            count=27;
        else
            count=keystream.get(keystream.size()-1);

        int top = keystream.get(keystream.size()-1);
        keystream.remove(keystream.size()-1);

        ArrayList<Integer> tmp=new ArrayList<>();
        tmp.addAll(keystream);

        while(count>keystream.size()){
            count=count-keystream.size();
        }

        for(int x=0; x<count; x++){

            left.add(keystream.get(x));
            tmp.remove(0);
        }

        right=tmp;

        keystream=new ArrayList<>();

        keystream.addAll(right);
        keystream.addAll(left);
        keystream.add(top);

        refresh();
    }

    private void moveJokers(){
        locateJokers();

        if(j1Index==keystream.size()-1) {
            keystream.add(1, keystream.get(j1Index));
            keystream.remove(j1Index+1);
        }
        else
            Collections.swap(keystream, j1Index, j1Index+1);

        if(j2Index==keystream.size()-1){
            locateJokers();

            keystream.add(2,keystream.get(j2Index));
            keystream.remove(j2Index+1);
        }

        else if(j2Index==keystream.size()-2){
            locateJokers();

            keystream.add(1,keystream.get(j2Index));
            keystream.remove(j2Index+1);
        }
        else{
            locateJokers();

            keystream.add(j2Index+3,keystream.get(j2Index));
            keystream.remove(j2Index);
        }
    }

    public void run(){
            moveJokers();
            firstCut();
            secondCut();
    }

    private void refresh(){
        left=new ArrayList<>();
        middle=new ArrayList<>();
        right=new ArrayList<>();
    }

    private void locateJokers(){
        for(int x=0; x<keystream.size(); x++){

            if(keystream.get(x)==-1)
                j1Index=x;

            if(keystream.get(x)==-2)
                j2Index=x;
        }
    }

    public ArrayList<Integer> getKeystream() {
        return keystream;
    }
}

