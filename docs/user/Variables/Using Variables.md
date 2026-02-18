# Using Variables
This document describes usage of variables, while relatively straightforward and intuitive by design, there are some useful things to know about with Variables.


## String variables
Any JSON file imported (except for raw imports) can utilize variables using `{...}` tags, for instance `{example}` would be replaced with the value of `example`

With String fields in JSON files, you can also use variable replacement as part of a full string, eg. `{"example":"Hello {example.world}"}` can become `{"example":"Hello World"}` when `example.world` is set to `World`.

Or with a more real-world example, for instance a URL: `https://{example.domain}/{example.uri}` where `example.domain` is set to `example.com` and `example.uri` is set to `example/path` the URL becomes `https://example.com/example/path`, just keep in mind that variable replacement does not format the result string, so eg. `example.uri` set to `/hello/world` would result in `https://example.com//hello/world`.


## Json elements as variable
Under the hood, the Json Variables system stores variables as Json Elements, which means variables can also return actual json Elements.

By default, if a value only has one variable replacement tag, eg. `{"example":"{element.value}"}` and no other string around it, the variable processor instead of actually returning a string, returns the raw element.

This allows returning actual json elements instead of string values:

Example input:
```json
{
    "hello": "World",
    "example": {
        "value1": "test",
        "value2": {
            "hello": "object"
        },

        "value3": "{example.value}"
    }
}
```

Here, `example` -> `value3` is a single string with only one variable replacement, since the string only has one and no other text around the variable, the engine replaces the whole element instead of the string value.

So when `example.value` instead is, for instance, a json aray, the value of `value3` becomes the array.

With eg. `["hello","world"]` as value for `example.value`, the above json beocmes:

```json
{
    "hello": "World",
    "example": {
        "value1": "test",
        "value2": {
            "hello": "object"
        },

        "value3": ["hello", "world"]
    }
}
```

This allows for more powerful variable injection.


## When are variables resolved?
Variable interpretation happens when the element is accessed, meaning that the value is processed Just In Time, when the element value is read, and not at parsing time.

This means that even if `example.value3` is not assigned while the script is being parsed, as long as it is assigned prior to the Target Tool (eg. a json emitter) runs, eg. by a plugin, the value will still be resolved.