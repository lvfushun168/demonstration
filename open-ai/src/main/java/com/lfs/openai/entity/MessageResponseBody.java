package com.lfs.openai.entity;
import lombok.Data;

import java.util.List;

/**
 * @author lfs
 */
@Data
public class MessageResponseBody {

    private String id;

    private String object;

    private int create;

    private String model;

    private List<Choices> choices;

}
