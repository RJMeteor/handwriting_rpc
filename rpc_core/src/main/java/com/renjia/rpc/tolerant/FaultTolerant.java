package com.renjia.rpc.tolerant;

import lombok.Getter;

import java.io.Serializable;

public interface FaultTolerant {

    Object tolerant();
}
