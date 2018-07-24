package com.eshore.fileExport;
public class Starter{
    public static void main( String[] args ){
    	new ConfigWatcher().init();
    	new MapRegister().init();
        new DbToFile().work();
    }
}
