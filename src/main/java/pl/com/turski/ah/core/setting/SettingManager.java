package pl.com.turski.ah.core.setting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Component;
import pl.com.turski.ah.model.setting.FtpSetting;
import pl.com.turski.ah.model.setting.GallerySetting;
import pl.com.turski.ah.model.setting.Setting;
import pl.com.turski.ah.service.FtpService;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * User: Adam
 */
@Component
public class SettingManager {

    @Autowired
    private Resource settingFile;
    @Autowired
    private FtpService ftpService;

    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    private FtpSetting ftpSetting;
    private GallerySetting gallerySetting;

    public void saveSettings(FtpSetting ftpSetting, GallerySetting gallerySetting) throws IOException, XmlMappingException {
        FileOutputStream os = null;
        try {
            Setting setting = new Setting();
            setting.setFtpSetting(ftpSetting);
            setting.setGallerySetting(gallerySetting);
            this.ftpSetting = ftpSetting;
            this.gallerySetting = gallerySetting;
            os = new FileOutputStream(settingFile.getFile());
            this.marshaller.marshal(setting, new StreamResult(os));
            ftpService.disconnect();
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    public void loadSettings() throws IOException, XmlMappingException {
        FileInputStream is = null;
        try {
            is = new FileInputStream(settingFile.getFile());
            Setting setting = (Setting) this.unmarshaller.unmarshal(new StreamSource(is));
            ftpSetting = setting.getFtpSetting();
            gallerySetting = setting.getGallerySetting();
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public FtpSetting getFtpSetting() {
        return ftpSetting;
    }

    public GallerySetting getGallerySetting() {
        return gallerySetting;
    }
}
