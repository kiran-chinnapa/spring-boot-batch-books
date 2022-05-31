package com.batch.books.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.kafka.KafkaItemReader;
import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.batch.item.kafka.builder.KafkaItemReaderBuilder;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;

@Configuration
@EnableBatchProcessing
public class ConsumerJobConfig {

    Logger log = LoggerFactory.getLogger(ConsumerJobConfig.class);

    @PostConstruct
    void initialize(){
        log.info("Consumer initialized");
    }

    @Bean
    public Job streamItemReader(JobBuilderFactory jobBuilderFactory,
                             StepBuilderFactory stepBuilderFactory,
                             ItemReader<String> itemReader,
                             ItemProcessor<String, String> itemProcessor,
                             ItemWriter<String> itemWriter) {

//        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//        taskExecutor.setCorePoolSize(5);
//        taskExecutor.setMaxPoolSize(5);
//        taskExecutor.afterPropertiesSet();

        Step step = stepBuilderFactory.get("fileReader-ETL")
                .<String, String>chunk(1)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
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
//    public KafkaItemReader<String,String> kafkaItemReader0() {
//        KafkaItemReader<String,String> kafkaItemReader = new KafkaItemReader<>();
//
//        FlatFileItemReader<String> flatFileItemReader = new FlatFileItemReader<>();
//        flatFileItemReader.setResource(new FileSystemResource(filePath));
//        flatFileItemReader.setName("file-json-reader");
//        flatFileItemReader.setLineMapper(new PassThroughLineMapper());
//        return flatFileItemReader;
//    }


    @Bean
    KafkaItemReader<String, String> kafkaItemReader(KafkaProperties properties) {
        Properties props = new Properties();
        props.putAll(properties.buildConsumerProperties());
        return new KafkaItemReaderBuilder<String, String>()
                .partitions(0)
                .consumerProperties(props)
                .name("customer-reader")
                .saveState(true)
                .topic("quick-events")
                .build();
    }

    @Bean
    Job job(StepBuilderFactory stepBuilderFactory,
             JobBuilderFactory jobBuilderFactory) {
//        KafkaItemWriter<String,String> kafkaItemWriter = new KafkaItemWriter<>();
        ItemWriter writer = new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items)
                    throws Exception {
                items.forEach(item ->
                        System.out.println("Consumed Message " + item));
            }
        };
        Step step = stepBuilderFactory.get("job")
                .chunk(0)
                .reader(kafkaItemReader(new KafkaProperties()))
                .writer(writer)
                .build();
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }
}
