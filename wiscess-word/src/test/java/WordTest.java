import com.wiscess.exporter.util.WordExportUtil;
import com.wiscess.exporter.word.AssignedRun;
import com.wiscess.exporter.word.IWordElement;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WordTest {

    @Test
    public void wordTest() {
        log.info("word测试");

    }

    @Test
    public void contetnTypeTest() throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream("D:\\GitHome\\wiscess-word\\newfile.docx");
        Map<String, IWordElement> map = new HashMap<String, IWordElement>();
        map.put("key", new AssignedRun("newValue"));
        WordExportUtil.export(fos, "/test.docx", map);
    }
}
