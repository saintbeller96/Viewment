package com.web.curation.recommend;

import com.web.curation.recommend.algorithm.FPGrowth;
import com.web.curation.recommend.algorithm.Rule;
import com.web.curation.recommend.analyzer.Analyzer;
import com.web.curation.recommend.analyzer.FpgAnalyzer;
import com.web.curation.repository.like.LikeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.Timestamp;
import java.util.*;



public class FPGrowthTest {
    @Mock
    LikeRepository likeRepository;

    @Test
    void test() throws Exception{

        long time = System.currentTimeMillis()-1000L*60L*60L*24L*7L;

        Timestamp t = new Timestamp(time);
        System.out.println(t);

        //given
        List<List<Long>> dataset = new ArrayList<>();
        dataset.add(Arrays.asList(1L, 2L, 3L, 4L));
        dataset.add(Arrays.asList(1L, 2L, 3L, 5L));
        dataset.add(Arrays.asList(2L, 5L, 6L));
        dataset.add(Arrays.asList(1L, 2L, 4L));
        dataset.add(Arrays.asList(1L, 3L, 6L, 7L));

        List<List<String>> dataset2 = new ArrayList<>();
        dataset2.add(Arrays.asList("a", "b", "c", "d"));
        dataset2.add(Arrays.asList("a", "b", "c", "e"));
        dataset2.add(Arrays.asList("b", "e", "f"));
        dataset2.add(Arrays.asList("a", "b", "d"));
        dataset2.add(Arrays.asList("a", "c", "f", "g"));

        List<Long> input = Arrays.asList(1L, 3L);
        //when

        FPGrowth fpGrowth = new FPGrowth<Long>(2);
        fpGrowth.setGlobalTree(dataset);
        fpGrowth.mine();
        Map allRules = fpGrowth.getAllRules();

        //then
        allRules.forEach((key, value)->{
            System.out.println(value);
        });

        Analyzer analyzer = new FpgAnalyzer();

        Set<Long> analysis = analyzer.analysis(input, dataset);
        System.out.println(analysis);

    }
}
