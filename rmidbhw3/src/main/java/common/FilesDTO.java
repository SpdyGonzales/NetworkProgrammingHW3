package common;

import java.io.Serializable;

public interface FilesDTO extends Serializable {

    public int getSize();

    public String getOwner();

    public String getFileName();

    public boolean getReadPermission();
    public boolean getWritePermission();
}
