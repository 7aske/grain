package com._7aske.grain.web.http.codec.json;

import com._7aske.grain.web.http.codec.json.nodes.JsonNode;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTests {

    @Test
    void test_validJsonObjectParsed() {
        JsonParser deserializer = new JsonParser();
        String json = "{\"valid_auth\":false,\"count\":1,\"limit\":1,\"total\":139,\"last_page\":139,\"result\":[{\"id\":2582,\"cospar_id\":\"\",\"sort_date\":\"1631750396\",\"name\":\"Inspiration4\",\"provider\":{\"id\":1,\"name\":\"SpaceX\",\"slug\":\"spacex\"},\"vehicle\":{\"id\":1,\"name\":\"Falcon 9\",\"company_id\":1,\"slug\":\"falcon-9\"},\"pad\":{\"id\":2,\"name\":\"LC-39A\",\"location\":{\"id\":61,\"name\":\"Kennedy Space Center\",\"state\":\"FL\",\"statename\":\"Florida\",\"country\":\"United States\",\"slug\":\"kennedy-space-center\"}},\"missions\":[{\"id\":4026,\"name\":\"Inspiration4\",\"description\":null}],\"mission_description\":null,\"launch_description\":\"A SpaceX Falcon 9 rocket will launch the Inspiration4 mission. The launch date is currently targeted for September 15, 2021 (UTC).\",\"win_open\":null,\"t0\":null,\"win_close\":null,\"est_date\":{\"month\":9,\"day\":15,\"year\":2021,\"quarter\":null},\"date_str\":\"Sep 15\",\"tags\":[{\"id\":9,\"text\":\"Crewed\"},{\"id\":18,\"text\":\"Tourism\"}],\"slug\":\"inspiration4\",\"weather_summary\":null,\"weather_temp\":null,\"weather_condition\":null,\"weather_wind_mph\":null,\"weather_icon\":null,\"weather_updated\":null,\"quicktext\":\"Falcon 9 - Inspiration4 - Sep 15 (estimated) - https:\\/\\/rocketlaunch.live\\/launch\\/inspiration4 for info\\/stream\",\"media\":[],\"result\":-1,\"suborbital\":false,\"modified\":\"2021-03-30T14:27:13+00:00\"}], \"empty\":{}}";
        JsonNode test = deserializer.parse(json);
        System.out.println(test);
        assertFalse(test.get("valid_auth").getBoolean());
        assertEquals(1, test.get("count").getValue());
        assertEquals(1, test.get("limit").getValue());
        assertEquals(139, test.get("total").getValue());
        assertEquals(2582, test.get("result").asArray().get(0).get("id").getValue());
        assertEquals("", test.get("result").asArray().get(0).get("cospar_id").getValue());
        assertEquals("1631750396", test.get("result").asArray().get(0).get("sort_date").getValue());
        assertEquals("Inspiration4", test.get("result").asArray().get(0).get("name").getValue());
        assertEquals(1, test.get("result").asArray().get(0).get("provider").get("id").getValue());
        assertEquals("SpaceX", test.get("result").asArray().get(0).get("provider").get("name").getValue());
        assertEquals(1, test.get("result").asArray().get(0).get("vehicle").get("id").getValue());
        assertEquals("Falcon 9", test.get("result").asArray().get(0).get("vehicle").get("name").getValue());
        assertEquals(4026, test.get("result").asArray().get(0).get("missions").asArray().get(0).get("id").getValue());
        assertNull(test.get("result").asArray().get(0).get("missions").asArray().get(0).get("description").getValue());
        assertEquals(18, test.get("result").asArray().get(0).get("tags").asArray().get(1).get("id").getValue());
    }

    @Test
    void test_emoji() {
        JsonParser deserializer = new JsonParser();
        String json = "{\"emoji\": \" 👍 \"}";
        JsonNode test = deserializer.parse(json);
        System.out.println(test);
        assertEquals(" 👍 ", test.get("emoji").getValue());
    }

    @Test
    void test_utf8() {
        String longText = "Sentences that contain all letters commonly used in a language\\n                --------------------------------------------------------------\\n                    \\n                Markus Kuhn <http://www.cl.cam.ac.uk/~mgk25/> -- 2012-04-11\\n                    \\n                This is an example of a plain-text file encoded in UTF-8.\\n                    \\n                    \\n                Danish (da)\\n                ---------\\n                    \\n                  Quizdeltagerne spiste jordbær med fløde, mens cirkusklovnen\\n                  Wolther spillede på xylofon.\\n                  (= Quiz contestants were eating strawbery with cream while Wolther\\n                  the circus clown played on xylophone.)\\n                    \\n                German (de)\\n                -----------\\n                    \\n                  Falsches Üben von Xylophonmusik quält jeden größeren Zwerg\\n                  (= Wrongful practicing of xylophone music tortures every larger dwarf)\\n                    \\n                  Zwölf Boxkämpfer jagten Eva quer über den Sylter Deich\\n                  (= Twelve boxing fighters hunted Eva across the dike of Sylt)\\n                    \\n                  Heizölrückstoßabdämpfung\\n                  (= fuel oil recoil absorber)\\n                  (jqvwxy missing, but all non-ASCII letters in one word)\\n                    \\n                Greek (el)\\n                ----------\\n                    \\n                  Γαζέες καὶ μυρτιὲς δὲν θὰ βρῶ πιὰ στὸ χρυσαφὶ ξέφωτο\\n                  (= No more shall I see acacias or myrtles in the golden clearing)\\n                    \\n                  Ξεσκεπάζω τὴν ψυχοφθόρα βδελυγμία\\n                  (= I uncover the soul-destroying abhorrence)\\n                    \\n                English (en)\\n                ------------\\n                    \\n                  The quick brown fox jumps over the lazy dog\\n                    \\n                Spanish (es)\\n                ------------\\n                    \\n                  El pingüino Wenceslao hizo kilómetros bajo exhaustiva lluvia y\s\\n                  frío, añoraba a su querido cachorro.\\n                  (Contains every letter and every accent, but not every combination\\n                  of vowel + acute.)\\n                    \\n                French (fr)\\n                -----------\\n                    \\n                  Portez ce vieux whisky au juge blond qui fume sur son île intérieure, à\\n                  côté de l'alcôve ovoïde, où les bûches se consument dans l'âtre, ce\\n                  qui lui permet de penser à la cænogenèse de l'être dont il est question\\n                  dans la cause ambiguë entendue à Moÿ, dans un capharnaüm qui,\\n                  pense-t-il, diminue çà et là la qualité de son œuvre.\s\\n                    \\n                  l'île exiguë\\n                  Où l'obèse jury mûr\\n                  Fête l'haï volapük,\\n                  Âne ex aéquo au whist,\\n                  Ôtez ce vœu déçu.\\n                    \\n                  Le cœur déçu mais l'âme plutôt naïve, Louÿs rêva de crapaüter en\\n                  canoë au delà des îles, près du mälström où brûlent les novæ.\\n                    \\n                Irish Gaelic (ga)\\n                -----------------\\n                    \\n                  D'fhuascail Íosa, Úrmhac na hÓighe Beannaithe, pór Éava agus Ádhaimh\\n                    \\n                Hungarian (hu)\\n                --------------\\n                    \\n                  Árvíztűrő tükörfúrógép\\n                  (= flood-proof mirror-drilling machine, only all non-ASCII letters)\\n                    \\n                Icelandic (is)\\n                --------------\\n                    \\n                  Kæmi ný öxi hér ykist þjófum nú bæði víl og ádrepa\\n                    \\n                  Sævör grét áðan því úlpan var ónýt\\n                  (some ASCII letters missing)\\n                    \\n                Japanese (jp)\\n                -------------\\n                    \\n                  Hiragana: (Iroha)\\n                    \\n                  いろはにほへとちりぬるを\\n                  わかよたれそつねならむ\\n                  うゐのおくやまけふこえて\\n                  あさきゆめみしゑひもせす\\n                    \\n                  Katakana:\\n                    \\n                  イロハニホヘト チリヌルヲ ワカヨタレソ ツネナラム\\n                  ウヰノオクヤマ ケフコエテ アサキユメミシ ヱヒモセスン\\n                    \\n                Hebrew (iw)\\n                -----------\\n                    \\n                  ? דג סקרן שט בים מאוכזב ולפתע מצא לו חברה איך הקליטה\\n                    \\n                Polish (pl)\\n                -----------\\n                    \\n                  Pchnąć w tę łódź jeża lub ośm skrzyń fig\\n                  (= To push a hedgehog or eight bins of figs in this boat)\\n                    \\n                Russian (ru)\\n                ------------\\n                    \\n                  В чащах юга жил бы цитрус? Да, но фальшивый экземпляр!\\n                  (= Would a citrus live in the bushes of south? Yes, but only a fake one!)\\n                    \\n                  Съешь же ещё этих мягких французских булок да выпей чаю\\n                  (= Eat some more of these fresh French loafs and have some tea)\s\\n                    \\n                Thai (th)\\n                ---------\\n                    \\n                  [--------------------------|------------------------]\\n                  ๏ เป็นมนุษย์สุดประเสริฐเลิศคุณค่า  กว่าบรรดาฝูงสัตว์เดรัจฉาน\\n                  จงฝ่าฟันพัฒนาวิชาการ           อย่าล้างผลาญฤๅเข่นฆ่าบีฑาใคร\\n                  ไม่ถือโทษโกรธแช่งซัดฮึดฮัดด่า     หัดอภัยเหมือนกีฬาอัชฌาสัย\\n                  ปฏิบัติประพฤติกฎกำหนดใจ        พูดจาให้จ๊ะๆ จ๋าๆ น่าฟังเอย ฯ\\n                    \\n                  [The copyright for the Thai example is owned by The Computer\\n                  Association of Thailand under the Royal Patronage of His Majesty the\\n                  King.]\\n                    \\n                Turkish (tr)\\n                ------------\\n                    \\n                  Pijamalı hasta, yağız şoföre çabucak güvendi.\\n                  (=Patient with pajamas, trusted swarthy driver quickly)\\n                    \\n                    \\n                Special thanks to the people from all over the world who contributed\\n                these sentences since 1999.\\n                    \\n                A much larger collection of such pangrams is now available at\\n                    \\n                  http://en.wikipedia.org/wiki/List_of_pangrams";
        String jsonString = "{\"longText\":\"" + longText + "\"}";

        JsonParser deserializer = new JsonParser();
        JsonNode test = deserializer.parse(jsonString);
        System.out.println(test);

        assertEquals(longText, test.get("longText").getValue());
    }

    static final class Data {
        int number;
    }

    public static void testMethodListParameter(List<List<Data>> data) {
    }

    public static void testMethodMapParameter(Map<String, List<Data>> data) {
    }

    @Test
    void test_listMapping() throws Exception {
        Method method = JsonParserTests.class.getMethod("testMethodListParameter", List.class);
        Parameter parameter = method.getParameters()[0];

        String jsonString = "[[{\"number\": 1}]]";

        JsonMapper mapper = new JsonMapper();
        JsonParser parser = new JsonParser();
        List<List<Data>> test = (List<List<Data>>) mapper.mapValue(parser.parse(jsonString), parameter);
        System.out.println(test);

        assertEquals(1, test.get(0).get(0).number);
    }

    @Test
    void test_mapMapping() throws Exception {
        Method method = JsonParserTests.class.getMethod("testMethodMapParameter", Map.class);
        Parameter parameter = method.getParameters()[0];

        String jsonString = "{\"data\": [{\"number\": 1}]}";

        JsonMapper mapper = new JsonMapper();
        JsonParser parser = new JsonParser();
        Map<String, List<Data>> test = (Map<String, List<Data>>) mapper.mapValue(parser.parse(jsonString), parameter);
        System.out.println(test);

        assertEquals(1, test.get("data").get(0).number);
    }
}