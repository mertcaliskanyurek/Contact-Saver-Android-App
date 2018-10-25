package com.mcaliskanyurek.TextFileHelper;

import java.util.ArrayList;

public interface ITextDataParser<T> {

    ArrayList<T> parseData(ArrayList<String> lines);
}
