import com.emu.wav.WavHeader;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.fail;


public class WavHeaderTests {

    @Test
    public void TestValidHeader() throws IOException {
        byte[] validHeader = {82, 73, 70, 70, -112, 66, 14, 0, 87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0,
                2, 0, 68, -84, 0, 0, 16, -79, 2, 0, 4, 0, 16, 0, 100, 97, 116, 97, 108, 66, 14, 0};
        InputStream streamIn = new ByteArrayInputStream(validHeader);
        WavHeader testHeader = new WavHeader(streamIn);
        Assert.assertArrayEquals(validHeader, testHeader.getBytes());
    }

    @Test
    public void TestInvalidHeader() throws IOException {
        byte[] invalidHeader = {99, 74, 70, 70, -112, 66, 14, 0, 87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0,
                2, 0, 68, -84, 0, 0, 16, -79, 2, 0, 4, 0, 16, 0, 100, 97, 116, 97, 108, 66, 14, 0};
        InputStream streamIn = new ByteArrayInputStream(invalidHeader);
        try{
            WavHeader testHeader = new WavHeader(streamIn);
            // We should throw an exception here
            fail("Header constructor should have thrown an error");
        }catch (IOException exc){
            // Success
        }
    }

}
