package io.kyligence.benchmark.loadtest.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiefan on 17-1-4.
 */
public class FullRegressionJob implements Job {

    private TestCase testCase;

    public FullRegressionJob(TestCase testCase) {
        this.testCase = testCase;
    }

    private static final Logger logger = LoggerFactory.getLogger(FullRegressionJob.class);

    private List<Job> jobs = new ArrayList<>();

    @Override
    public boolean run() throws Exception {
        //build cube
        long startTime = System.currentTimeMillis();
        boolean success = true;
        if (testCase.isNeedRebuildCube()) {
            Job buildCubeJob = new BuildCubeJob(testCase);
            jobs.add(buildCubeJob);
            success = buildCubeJob.run();
            logger.info("BuildCubeJob finish time : {}", System.currentTimeMillis() - startTime);
            if (!success) {
                throw new Exception("FullRegression job dead in BuildCubeJob procedure");
            }
            //buildCubeJob.dump();
        } else {
            logger.info("Skip BuildCubeJob");
        }
        //benchmark test
        if (testCase.isRunBenchmarkTest()) {
            startTime = System.currentTimeMillis();
            Job benchmarkTestJob = new BenchmarkTestJob(testCase);
            jobs.add(benchmarkTestJob);
            success = benchmarkTestJob.run();
            logger.info("BenchmarkTestJob finish time : {}", System.currentTimeMillis() - startTime);
            if (!success) {
                throw new Exception("FullRegression job dead in BenchmarkTestJob procedure");
            }
            //benchmarkTestJob.dump();
        } else {
            logger.info("Skip BenchmarkTestJob");
        }
        //stress test
        if (testCase.isRunStressTest()) {
            startTime = System.currentTimeMillis();
            Job stressTestJob = new LoadTestJob(testCase);
            jobs.add(stressTestJob);
            success = stressTestJob.run();
            logger.info("StressTestJob finish time : {}", System.currentTimeMillis() - startTime);
            if (!success) {
                throw new Exception("FullRegression job dead in StressTestJob procedure");
            }
            //stressTestJob.dump();
        } else {
            logger.info("Skip StressTestJob");
        }
        return true;
    }

    @Override
    public void dump() {
        for (Job j : jobs) {
            j.dump();
        }
    }
}
