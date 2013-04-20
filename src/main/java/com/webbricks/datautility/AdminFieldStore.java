package com.webbricks.datautility;

import java.lang.annotation.*;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface AdminFieldStore
{
}