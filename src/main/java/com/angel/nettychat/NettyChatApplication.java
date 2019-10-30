package com.angel.nettychat;

import com.angel.nettychat.config.netty.WSServer;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyChatApplication.class, args);
    }
}
