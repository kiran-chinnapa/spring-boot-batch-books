package com.batch.books.config;

import com.batch.books.model.BookRecord;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,
                   StepBuilderFactory stepBuilderFactory,
                   ItemReader<String> mapItemReader,
                   ItemProcessor<String, BookRecord> mapItemProcessor,
                   ItemWriter<BookRecord> mapItemWriter) {

        Step step = stepBuilderFactory.get("openLibrary-ETL")
                .<String, BookRecord>chunk(100)
                .reader(mapItemReader)
                .processor(mapItemProcessor)
                .writer(mapItemWriter)
                .build();

        return jobBuilderFactory.get("openLibrary-Load")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();

    }

    @Bean
    public FlatFileItemReader<String> itemReader() {
        FlatFileItemReader<String> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource("src/main/resources/dumps/author_10_lines"));
        flatFileItemReader.setName("author-json-reader");
        flatFileItemReader.setLineMapper(new PassThroughLineMapper());
        return flatFileItemReader;
    }

}
