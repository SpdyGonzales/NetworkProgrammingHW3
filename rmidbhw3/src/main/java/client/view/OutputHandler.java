package client.view;


import common.FilesDTO;

public class OutputHandler {

    public void printNxtLine(String s){
        System.out.println(s);
    }
    public void printFile(FilesDTO file){
        System.out.println("File Name: " + file.getFileName());
        System.out.println("Owner: " + file.getFileName());
        System.out.println("Size:" + file.getSize());
        if(file.getReadPermission()){
            System.out.println("Read Permission: Yes");
        }else {
            System.out.println("Read Permission: No");
        }
        if(file.getWritePermission()){
            System.out.println("Write Permission: Yes");
        }else {
            System.out.println("Write Permission: No");
        }
    }
    public void printWelcome(){
        System.out.println("Welcome to File Transfer Service");
        System.out.println("Please login or Create account with commands:");
        System.out.println("Login 'username' 'password or");
        System.out.println("Create 'username' 'password'");

    }
}
