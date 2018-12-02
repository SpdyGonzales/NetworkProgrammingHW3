package server.model;

import common.FilesDTO;

import java.io.Serializable;

public class File implements Serializable, FilesDTO {
    private String fileName;
    private String owner;
    private int size;
    private boolean read;
    private boolean write;

    public File(String fileName, String owner, int size, boolean read, boolean write){
        this.fileName = fileName;
        this.owner = owner;
        this.size = size;
        this.read = read;
        this.write = write;
    }
    public File(String fileName){
        this.fileName = fileName;
    }

    public int getSize() {return size; }

    public String getOwner() { return owner; }

    public String getFileName(){return fileName; }

    public boolean getWritePermission() {
        return write;
    }
    public boolean getReadPermission() { return read;
    }
}
