package UtilsTest;

import org.junit.Test;
import ua.nick.weather.utils.NetUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class NetUtilsTest {

    private static String url;

    @Test
    public void test_urlToString_success() {
        url = "http://api.coindesk.com/v1/bpi/currentprice.json";
        String json = null;
        try {
            json = NetUtils.urlToString(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertThat(json, containsString(":"));
    }

    @Test(expected = IOException.class)
    public void test_urlToString_fail_errorUrl() throws IOException {
        url = "error_url";
        NetUtils.urlToString(new URL(url));
    }

    @Test(expected = MalformedURLException.class)
    public void test_urlToString_fail_urlIsNull() throws IOException {
        NetUtils.urlToString(new URL(null));
    }
}