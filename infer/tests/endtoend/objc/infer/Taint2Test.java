/*
 * Copyright (c) 2013 - present Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package endtoend.objc.infer;

import static org.hamcrest.MatcherAssert.assertThat;
import static utils.matchers.ResultContainsExactly.containsExactly;

import com.google.common.collect.ImmutableList;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;

import utils.DebuggableTemporaryFolder;
import utils.InferException;
import utils.InferResults;
import utils.InferRunner;

public class Taint2Test {

  public static final String TaintFile =
      "infer/tests/codetoanalyze/objc/errors/taint/sources.m";

  public static final String TAINTED_VALUE = "TAINTED_VALUE_REACHING_SENSITIVE_FUNCTION";

  private static ImmutableList<String> inferCmd;

  @ClassRule
  public static DebuggableTemporaryFolder folder = new DebuggableTemporaryFolder();


  @BeforeClass
  public static void runInfer() throws InterruptedException, IOException {
    inferCmd = InferRunner.createObjCInferCommandWithMLBuckets(
        folder,
        TaintFile,
        "cf",
        false);
  }

  @Test
  public void whenInferRunsOnTaintFileErrorFound()
      throws InterruptedException, IOException, InferException {
    InferResults inferResults = InferRunner.runInferObjC(inferCmd);
    String[] methods = {
        "testNSHTTPCookie1",
        "testNSHTTPCookie2",
        "testNSHTTPCookie3",
        "testNSHTTPCookie4"
    };

    assertThat(
        "Results should contain tainted value reaching sensitive function.",
        inferResults,
        containsExactly(
            TAINTED_VALUE,
            TaintFile,
            methods
        )
    );
  }

}
