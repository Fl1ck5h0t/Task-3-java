package org.example;

public enum ConsoleOperations {
    READ("--read"),
    DELETE("--delete"),
    UPDATE("--update"),
    CREATE("--create");
    private final String operation;

     ConsoleOperations(String operation) {
        this.operation = operation;
    }


    public String getOperation() {
        return operation;
    }

    public static ConsoleOperations valueOfOparation(String string){
         if(string.equals(READ.operation)){
             return READ;
         }else if(string.equals(DELETE.operation)){
             return DELETE;
         }else if(string.equals(UPDATE.operation)){
             return UPDATE;
         }else if(string.equals(CREATE.operation)){
             return CREATE;
         }else {
             throw new RuntimeException();
         }
    }
}
