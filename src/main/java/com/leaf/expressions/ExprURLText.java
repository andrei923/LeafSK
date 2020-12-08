package com.leaf.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;


public class ExprURLText extends SimplePropertyExpression<String, String> {

	static {
		Skript.registerExpression(ExprURLText.class, String.class, ExpressionType.SIMPLE, "text from [url] %string%");
	}	
	
    @Override
    protected String getPropertyName() {
        return "text from [url] %string%";
    }

    @SuppressWarnings("resource")
	@Override
    public String convert(String s) {
        try {
            URL url = new URL(s);
            Scanner a = new Scanner(url.openStream());
            String str = "";
            boolean first = true;
            while(a.hasNext()){
                if(first) str = a.next();
                else str += " " + a.next();
                first = false;
            }
            return str;
        } catch(IOException ex) {
            return null;
        }
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }
}