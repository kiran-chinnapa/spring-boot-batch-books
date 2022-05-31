package com.batch.books.config;

import com.batch.books.BooksApplication;
import com.batch.books.reader.RestApiReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//@Configuration
//@EnableBatchProcessing
public class JobConfiguration {

    @Value("${books.grid.chunk.size}")
    private int chunkSize;

    @Value("${books.grid.read.work.file.path}")
    private String workFilePath;

    @Value("${books.grid.read.author.file.path}")
    private String authorFilePath;

    @Value("${books.grid.read.edition.file.path}")
    private String editionFilePath;

    @Value("${grid.books.work.grid.id}")
    private String workGridId;

    @Value("${grid.books.edition.grid.id}")
    private String editionGridId;

    @Value("${grid.books.author.grid.id}")
    private String authorGridId;

    @Value("${grid.books.grid.id}")
    private String bookGridId;

    private String filePath="";

    @PostConstruct
    public void contructFilePath(){
        if ("work".equals(BooksApplication.gridType)) {
            System.setProperty("gridId", workGridId);
            filePath = workFilePath;

        } else if ("author".equals(BooksApplication.gridType)) {
            System.setProperty("gridId", authorGridId);
            filePath = authorFilePath;
        } else if ("edition".equals(BooksApplication.gridType)) {
            System.setProperty("gridId", editionGridId);
            filePath= editionFilePath;
        }
        else if ("book".equals(BooksApplication.gridType))
            System.setProperty("gridId", bookGridId);
    }

    @Bean
    public Step step0(StepBuilderFactory stepBuilderFactory){
        return stepBuilderFactory.get("step0")
                .tasklet(new Tasklet() {
                    Logger log = LoggerFactory.getLogger(JobConfiguration.class);
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        log.info("------------------step0 tasklet-----------------");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    @Bean
    public Job fileReaderJob(JobBuilderFactory jobBuilderFactory,
                   StepBuilderFactory stepBuilderFactory,
                   ItemReader<String> mapItemReader,
                   ItemProcessor<String, String> mapItemProcessor,
                   ItemWriter<String> mapItemWriter) {

//        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//        taskExecutor.setCorePoolSize(5);
//        taskExecutor.setMaxPoolSize(5);
//        taskExecutor.afterPropertiesSet();

        Step step = stepBuilderFactory.get("fileReader-ETL")
                .<String, String>chunk(chunkSize)
                .reader(mapItemReader)
                .processor(mapItemProcessor)
                .writer(mapItemWriter)
//                .taskExecutor(taskExecutor)
                .build();

        return jobBuilderFactory.get("fileReader-Load")
                .incrementer(new RunIdIncrementer())
                .start(step)
//                .start(step0(stepBuilderFactory))
//                .next(step)
                .build();

    }

//    @Bean
//    public Job restApiReaderJob(JobBuilderFactory jobBuilderFactory,
//                   StepBuilderFactory stepBuilderFactory,
//                   ItemReader<Map<Object,Object>> mapItemReader,
//                   ItemProcessor<Map<Object,Object>, String> mapItemProcessor,
//                   ItemWriter<String> mapItemWriter) {
//
//        Step step = stepBuilderFactory.get("restApiReader-ETL")
//                .<Map<Object,Object>, String>chunk(chunkSize)
//                .reader(mapItemReader)
//                .processor(mapItemProcessor)
//                .writer(mapItemWriter)
//                .build();
//
//        return jobBuilderFactory.get("restApiReader-Load")
//                .incrementer(new RunIdIncrementer())
//                .start(step)
//                .build();
//
//    }

    @Bean
    public FlatFileItemReader<String> fileItemReader() {
        FlatFileItemReader<String> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(filePath));
        flatFileItemReader.setName("file-json-reader");
        flatFileItemReader.setLineMapper(new PassThroughLineMapper());
        return flatFileItemReader;
    }

}
