package ru.besttuts.finance.logic.yahoo.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ru.besttuts.finance.logic.yahoo.model.YahooFutures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author romanchekashov
 * @since 24.10.2016
 */
public class YahooFuturesDeserializer extends StdDeserializer<YahooFutures> {

    public YahooFuturesDeserializer(Class<?> vc) {
        super(vc);
    }

    public YahooFuturesDeserializer(JavaType valueType) {
        super(valueType);
    }

    public YahooFuturesDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public YahooFutures deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        YahooFutures yahooFutures = new YahooFutures();

        JsonNode node = p.getCodec().readTree(p);
        ArrayNode result = (ArrayNode) node.get("quoteSummary").get("result");
        ArrayNode futuresArrNode = (ArrayNode) result.get(0).get("futuresChain").get("futures");

        List<String> futures = new ArrayList<>(futuresArrNode.size());
        for (JsonNode future: futuresArrNode){
            futures.add(future.asText());
        }
        yahooFutures.setFutures(futures);

        return yahooFutures;
    }
}
