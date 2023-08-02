package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message
{
    private String operation;
    private String msg;


    public Message setOperation(String operation)
    {
        this.operation = operation;
        return this;
    }

    public Message setMsg(String msg)
    {

        this.msg = msg;
        return this;
    }
}

