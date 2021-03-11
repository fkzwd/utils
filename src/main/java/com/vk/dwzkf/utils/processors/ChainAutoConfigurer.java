package com.vk.dwzkf.utils.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChainAutoConfigurer implements BeanPostProcessor {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void collect(List<Object> beans) {
        Class<ProcessorChain> chainClass = ProcessorChain.class;
        Class<Processor> processorClass = Processor.class;
        Map<Class<ProcessorChain>, List<ProcessorChain>> chainMap =
                (Map<Class<ProcessorChain>, List<ProcessorChain>>) (Object) beans.stream()
                .filter(chainClass::isInstance)
                .collect(Collectors.groupingBy(Object::getClass));
        List<Processor> processors = (List<Processor>) (Object) beans.stream()
                .filter(processorClass::isInstance)
                .filter(object -> object.getClass().isAnnotationPresent(Chained.class))
                .collect(Collectors.toList());
        log.info("Collected chain map: {}", chainMap);
        log.info("Collected annotated processors: {}", processors);
        processors.forEach(processor -> {
            Class chainKlass = processor.getClass().getAnnotation(Chained.class).value();
            List<ProcessorChain> chain = chainMap.get(chainKlass);
            if (chain != null) {
                chain.forEach(processorChain -> {
                    Class chainProcessorTargetKlass = processorChain.getTargetClass();
                    Class processorTargetKlass = processor.getTargetClass();
                    if (chainProcessorTargetKlass != null && processorTargetKlass != null) {
                        if (chainProcessorTargetKlass.isAssignableFrom(processorTargetKlass)) {
                            if (processorChain.containsProcessor(processor)) {
                                log.info("Processor {} already included in chain {}",
                                        processor,
                                        processorChain
                                );
                            } else {
                                processorChain.addProcessor(processor);
                                log.info("Added processor {} to chain {}", processor, processorChain);
                            }
                        }
                    }
                });
            }
        });
    }
}
