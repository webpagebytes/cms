package com.webpagebytes.cms.datautility;

import java.lang.annotation.*;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface AdminFieldStore
{
}