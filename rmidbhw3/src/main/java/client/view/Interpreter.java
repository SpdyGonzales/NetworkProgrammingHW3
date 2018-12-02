package client.view;

import common.FileTransferService;
import common.FilesDTO;
import common.NotificationSystem;
import common.UserAccountDTO;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

public class Interpreter implements Runnable {
    private final Scanner console = new Scanner(System.in);
    private final OutputHandler output = new OutputHandler();
    private NotificationSys notifyOutput;
    private FileTransferService fts;
    private UserAccountDTO user = null;
    private boolean receivingCmds = false;
    private static final String PROMPT = "≈> ";

    public void start(FileTransferService fts) {
        this.fts = fts;
        try {
            this.notifyOutput = new NotificationSys();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        output.printWelcome();
        new Thread(this).start();
    }

    public void run() {
        while (receivingCmds) {
            try {
                output.printNxtLine(PROMPT);
                CommandHandler cmdLine = new CommandHandler(console.nextLine());
                Command cmd = cmdLine.getCmd();
                if (user == null && cmd.equals(Command.UPDATE_FILE) &&
                        cmd.equals(Command.ADD_FILE) &&
                        cmd.equals(Command.REMOVE_FILE) &&
                        cmd.equals(Command.UPDATE_FILE) &&
                        cmd.equals(Command.ILLEGAL_COMMAND)
                        ) {
                    output.printNxtLine("Please login or Create account with commands Login/Create 'username' 'password ");
                    continue;
                }
                switch (cmd) {
                    case LOGOUT:
                        if(user == null){
                            output.printNxtLine("You are already logged out");
                        }else{
                            fts.logoutAccount(user.getUsername());
                            user = null;
                        }
                        break;
                    case CREATE:
                        fts.createAccount(cmdLine.getParameter(0), cmdLine.getParameter(1));
                        break;
                    case LOGIN:
                        if(user == null) {
                            this.user = fts.loginAccount(cmdLine.getParameter(0), cmdLine.getParameter(1), this.notifyOutput);
                            if(user != null) {
                                output.printNxtLine("Welcome " + user.getUsername());
                                output.printNxtLine( "Your available commands are:");
                                output.printNxtLine("List_Files - To see all the files");
                                output.printNxtLine("Show_File 'name of file' - To see the description of a file you have read permission to");
                                output.printNxtLine("Remove_file 'name of file' ' - Delete File you have write permission to");
                                output.printNxtLine("Add_file 'name of file' 'Type Read to grant Read Permission' 'Type Write to grant Write Permission' - To Add a file");
                            }
                        }else{
                            output.printNxtLine("You are already logged in");
                        }
                        break;
                    case LIST_FILES:
                        List<? extends FilesDTO> files = fts.listFiles();
                        for (FilesDTO file : files) {
                            output.printNxtLine(file.getFileName());
                        }
                        break;
                    case REMOVE_FILE:
                        fts.removeFile(cmdLine.getParameter(0), user.getUsername());
                        output.printNxtLine("File Removed");
                        break;
                    case SHOW_FILE:
                        FilesDTO file = fts.fetchFile(cmdLine.getParameter(0), user.getUsername());
                        output.printFile(file);
                        break;
                    case ADD_FILE:
                        fts.addFile(cmdLine.getParameter(0), user.getUsername(),10, cmdLine.getParameter(1), cmdLine.getParameter(2));
                        output.printNxtLine("File Added");
                        break;
                    default:
                        output.printNxtLine("Command not allowed");
                }
            } catch (RemoteException e) {
                output.printNxtLine("Operation failed. Reason: ");
                output.printNxtLine(e.getMessage());
            }
        }
    }
    private class NotificationSys extends UnicastRemoteObject implements NotificationSystem {
        public NotificationSys() throws RemoteException {
        }

        public void notify(String message) throws RemoteException {
            output.printNxtLine(message);
            output.printNxtLine(PROMPT);
        }
    }
}
