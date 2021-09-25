package com.example.test;

import com.example.client.ClientLiveTest;
import com.example.client.RestClientLiveManualTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
// @formatter:off
    RestClientLiveManualTest.class
    , ClientLiveTest.class
}) //
public class LiveTestSuite {

}
