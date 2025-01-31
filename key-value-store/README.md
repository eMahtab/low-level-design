# KeyValueStore

Design a distributed key-value store that can perform the following

**Functional requirements**

1. Store a set of attributes (value) against a particular key (k)

2. Fetch the value stored against a particular key (k)

3. Delete a key (k)

4. Stretch - Perform a secondary index scan to fetch all key along with their
attributes where one of the attribute values is v.

!["Key Values"](images/key-value-store.png?raw=true)

Key can have a value consisting of multiple attributes.

Each attribute will have name, type associated (primitive types - boolean, double, integer, string) & type has to be identified at run time.

1) Key = delhi has 2 attributes only (pollution_level & population)
2) Key = jakarta has 3 attributes (latitude, longitude, pollution_level)
3) Key = bangalore has 4 attributes (extra - free_food)
4) Key = india has 2 attributes (capital & population)
5) Key = crocin has 2 attributes (category & manufacturer)

### Example of Secondary index:

Get all keys (cities) where pollution_level is high.

Get all medicines by manufacturer (GSK)

So, in a nutshell, value must be strongly typed when defined.

### Attribute
1. Attribute is uniquely identified by its name (latitude, longitude etc.

2. Data type of the attribute is defined at the first insert. (i.e. data type of pollution_level is set when key = delhi is inserted)

3. Once data type is associated with a particular attribute, it cannot be changed.
(i.e. free_food when defined takes type = boolean, hence, any key when using the attribute - free_food must allow only boolean values on subsequent inserts/updates)


