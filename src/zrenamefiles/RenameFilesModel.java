/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zrenamefiles;

import java.util.ArrayList;

/**
 *
 * @author jiajlu
 */
public class RenameFilesModel {
    public ArrayList<String> originalFileList;;
    public ArrayList<String> newFileList;
    public boolean showFullPathOriginal;
    public boolean showFullPathNew;
    
    public RenameFilesModel() {
        this.newFileList = new ArrayList<String>();
        this.originalFileList = new ArrayList<String>();
        showFullPathOriginal = true;
        showFullPathNew = true;
    }
}
