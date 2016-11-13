package sample;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

public class Controller {
    public ListView list;
    private OnTopJFileChooser chooser = new OnTopJFileChooser();
    private ObservableList<File> array;

    private class OnTopJFileChooser extends JFileChooser{
        protected JDialog createDialog(Component parent) throws HeadlessException {
            JDialog dialog = super.createDialog(parent);
            dialog.setAlwaysOnTop(true);
            return dialog;
        }
    }

    public void ChooseOnClick(ActionEvent actionEvent) {
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        int ref = chooser.showOpenDialog(null);



        if (ref == OnTopJFileChooser.APPROVE_OPTION) {

            File file = chooser.getSelectedFile();
            File[] files = file.listFiles();
            HashSet<File> arr = compareCheckSum(files);
            array = new ObservableListWrapper<File>(new ArrayList<>(arr));
            list.setItems(array);
        }
    }


    public void DeleteOnClick(ActionEvent actionEvent) {
        if (list.getItems().size()==0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Удаление");
            alert.setContentText("Фалы для удаления отсутствуют");
            alert.show();
        }
        else if(list.getItems().size()>0&&list.getSelectionModel().getSelectedItem()==null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Удаление");
            alert.setContentText("Выберите файл для удаления");
            alert.show();
        }
        else
            {
            File file = (File) list.getSelectionModel().getSelectedItem();

            if (list.getItems().size() > 1) {
                file = (File) list.getSelectionModel().getSelectedItem();
                file.delete();
                array.remove(file);

            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Подтверждающее окно");
                alert.setHeaderText("Удаление файла");
                alert.setContentText("Вы действительно хотите удалить последний экземпляр файла?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {

                    file.delete();
                    array.remove(file);
                    list.setItems(array);
                }
            }

        }
    }

    private HashSet<File> compareCheckSum(File[] files) {

        HashSet<File> set = new HashSet<>();
        for (int i = 0; i < files.length; i++) {
            for (int j = 0; j < files.length; j++) {
                if(!(files[j].isDirectory()||files[i].isDirectory())) {
                    if (i != j && getCheckSum(files[i]).equals(getCheckSum(files[j]))) {
                       set.add(files[j]);
                        set.add(files[i]);
                    }
                }
            }
        }
        return set;
    }

    private String getCheckSum(File file) {
        String FILENAME = file.getAbsolutePath();
        String ALGORITHM = "SHA-1";
        StringBuilder sb = new StringBuilder();
        try {
            // Получаем контрольную сумму для файла в виде массива байт
            final MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            final FileInputStream fis = new FileInputStream(FILENAME);
            byte[] dataBytes = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(dataBytes)) > 0) {
                md.update(dataBytes, 0, bytesRead);
            }
            byte[] mdBytes = md.digest();
            fis.close();

            // Переводим контрольную сумму в виде массива байт в
            // шестнадцатеричное представление

            for (int i = 0; i < mdBytes.length; i++) {
                sb.append(Integer.toString((mdBytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }

        } catch (NoSuchAlgorithmException | IOException ex) {

        }
        return sb.toString();
    }

}
