import dto.ResponseData;
import encoder.ImageResponseEncoder;
import encoder.ResponseEncoder;

public class ProtocolEncoder {
    public ResponseEncoder create(ResponseData encodeData) {



        return new ImageResponseEncoder();
    }
}
