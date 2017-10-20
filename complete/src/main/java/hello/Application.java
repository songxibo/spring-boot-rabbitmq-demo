package hello;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    final static String queueName = "notification.orderCancel.payment";
    final static String routingKey = "#.OrderTrackingLog.#";
    final static String exchangeName ="topic.notificationExchage";

    @Bean
    Queue queue() {
        return new Queue(queueName, true, false, false);
    }

    @Bean
    TopicExchange exchange() {
       // return new TopicExchange("spring-boot-exchange");
        
        //我们默认用得这个exchange
        Map<String,Object> map= new HashMap<String,Object>();
        map.put("x-delayed-type", "topic");
        TopicExchange exchange = new TopicExchange(exchangeName,true,false,map);
        exchange.setDelayed(true);
        
        return exchange;
        
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter, Queue queue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessageListener(listenerAdapter);
        container.setQueues(queue);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        
      //  System.in.read();
    }

}
