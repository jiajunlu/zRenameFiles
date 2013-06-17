/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zrenamefiles;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 *
 * @author jiajlu
 */
public class MainDialogController implements Initializable {
    
    @FXML
    private RenameFilesModel model;
    
    @FXML
    private ListView filesToBeRenamed;
    
    @FXML
    private ListView filesWillBeRenamedTo;
    
    @FXML
    private CheckBox chkOriginalFullPath;
    
    @FXML
    private CheckBox chkNewFullPath;
    
    @FXML
    private void handleChooseFilesAction(ActionEvent event) {
        // ObservableList<String> items = FXCollections.observableArrayList ();
                // ("陆家骏", "1吴 菲abc", "顿a顿b", "呦1a呦");
        
        FileChooser fc = new FileChooser();
        fc.setTitle("Select files to rename");
        List<File> files = fc.showOpenMultipleDialog(((Node)event.getTarget()).getScene().getWindow());
        
        for (File f : files) {
            model.originalFileList.add(f.getPath());
        }
        
        updateOriginalFileListView();
    }
    
    @FXML
    private void handlePreviewAction(ActionEvent event) {
        HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        
        model.newFileList.clear();
        for (String item : model.originalFileList) {
            File f = new File(item);
            String parentFolder = f.getParent();
            String fileName = f.getName();
           
            String pinyinStr = convertChineseStringToPinyinString(fileName, outputFormat);
            Path newFullPath = Paths.get(parentFolder, pinyinStr);
            model.newFileList.add(newFullPath.toString());
            
        }
        
        updateNewFileListView();
    }
    
    @FXML
    private void handleRenameAction(ActionEvent event) {
        try {
            for ( int i = 0; i < model.originalFileList.size(); i++) {
                File f1 = new File(model.originalFileList.get(i));
                File f2 = new File(model.newFileList.get(i));
                
                if(!f1.exists()) {
                    System.out.printf("File %s does not exist.", f1.getAbsolutePath());
                }
                boolean success = f1.renameTo(f2);
                if(success){
			System.out.println("Rename file succesful");
		}else{
			System.out.println("Rename failed");
		}
            }
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        // updateNewFileListView();
    }
    
    @FXML
    private void handleCheckOriginalListViewFullPath(ActionEvent event) {
        if (event.getSource() instanceof CheckBox) {
            CheckBox chk = (CheckBox) event.getSource();            
            model.showFullPathOriginal = chk.isSelected();
            updateOriginalFileListView();
        }
    }
    
    @FXML
    private void handleCheckPreviewListViewFullPath(ActionEvent event) {
        if (event.getSource() instanceof CheckBox) {
            CheckBox chk = (CheckBox) event.getSource();            
            model.showFullPathNew = chk.isSelected();
            updateNewFileListView();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model = new RenameFilesModel();
        updateView();
    }    
    
    private void updateView() {
        chkOriginalFullPath.setSelected(model.showFullPathOriginal);
        chkNewFullPath.setSelected(model.showFullPathNew);
        updateOriginalFileListView();
        updateNewFileListView();
    }
    
    private void updateOriginalFileListView()
    {
        ObservableList<String> items = FXCollections.observableArrayList ();
        for (String item : model.originalFileList) {
            if (model.showFullPathOriginal) {
                items.add(item);
            }
            else {
                items.add(new File(item).getName());
            }
        }
        
        filesToBeRenamed.setItems(items);
    }
    
    private void updateNewFileListView()
    {
        ObservableList<String> items = FXCollections.observableArrayList ();
        for (String item : model.newFileList) {
            if (model.showFullPathNew) {
                items.add(item);
            }
            else {
                items.add(new File(item).getName());
            }
        }
        
        filesWillBeRenamedTo.setItems(items);
    }

    private String convertChineseStringToPinyinString(String item, HanyuPinyinOutputFormat outputFormat) {
        char[] charArray = item.toCharArray();
        StringBuilder pinyinStrBuf = new StringBuilder();
        boolean isPrevCharChinese = false;
        boolean isPrevCharWhiteSpace = true;
        for (char chineseCharacter : charArray) {
            if (chineseCharacter == ' ' || chineseCharacter == '.') {
                isPrevCharChinese = false;
                isPrevCharWhiteSpace = true;
                pinyinStrBuf.append(chineseCharacter);
            }
            else {
                
                String[] pinyinArray = null;
                try {
                    pinyinArray = PinyinHelper.toHanyuPinyinStringArray(chineseCharacter, outputFormat);
                }
                catch (BadHanyuPinyinOutputFormatCombination e1) {
                    e1.printStackTrace();
                }
                if (pinyinArray != null) {
                    // Chinese char 
                    if (!isPrevCharWhiteSpace) {
                        pinyinStrBuf.append(" ");
                    }   
                    
                    pinyinStrBuf.append(pinyinArray[0].toUpperCase()); // only use the first one
                    isPrevCharChinese = true;
                }
                else {
                    // non-Chinese char
                    if (isPrevCharChinese) {
                        pinyinStrBuf.append(" ");   
                    }
                    
                    pinyinStrBuf.append(chineseCharacter);
                    isPrevCharChinese = false;
                }
                isPrevCharWhiteSpace = false;
            }
        }
        return pinyinStrBuf.toString();
    }
}
