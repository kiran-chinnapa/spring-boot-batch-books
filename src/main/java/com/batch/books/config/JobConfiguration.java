package com.batch.books.config;

import com.batch.books.BooksApplication;
import com.batch.books.reader.RestApiReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
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
    protected String workGridId;

    @Value("${grid.books.edition.grid.id}")
    protected String editionGridId;

    @Value("${grid.books.author.grid.id}")
    protected String authorGridId;

    private String filePath="";

    @PostConstruct
    void contructFilePath(){
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
    }

    @Bean
    public Job fileReaderJob(JobBuilderFactory jobBuilderFactory,
                   StepBuilderFactory stepBuilderFactory,
                   ItemReader<String> mapItemReader,
                   ItemProcessor<String, String> mapItemProcessor,
                   ItemWriter<String> mapItemWriter) {

        Step step = stepBuilderFactory.get("fileReader-ETL")
                .<String, String>chunk(chunkSize)
                .reader(mapItemReader)
                .processor(mapItemProcessor)
                .writer(mapItemWriter)
                .build();

        return jobBuilderFactory.get("fileReader-Load")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();

    }

    @Bean
    public Job restApiReaderJob(JobBuilderFactory jobBuilderFactory,
                   StepBuilderFactory stepBuilderFactory,
                   ItemReader<Map<Object,Object>> mapItemReader,
                   ItemProcessor<Map<Object,Object>, String> mapItemProcessor,
                   ItemWriter<String> mapItemWriter) {

        Step step = stepBuilderFactory.get("restApiReader-ETL")
                .<Map<Object,Object>, String>chunk(chunkSize)
                .reader(mapItemReader)
                .processor(mapItemProcessor)
                .writer(mapItemWriter)
                .build();

        return jobBuilderFactory.get("restApiReader-Load")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();

    }

    @Bean
    public FlatFileItemReader<String> fileItemReader() {
        FlatFileItemReader<String> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(filePath));
        flatFileItemReader.setName("file-json-reader");
        flatFileItemReader.setLineMapper(new PassThroughLineMapper());
        return flatFileItemReader;
    }

}
