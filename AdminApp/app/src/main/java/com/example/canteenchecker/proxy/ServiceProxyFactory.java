package com.example.canteenchecker.proxy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceProxyFactory {

    public static ServiceProxy create() {
      return new ServiceProxyImpl();
    }

}
