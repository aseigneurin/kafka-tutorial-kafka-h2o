# Kafka tutorial - Kafka + H2O

[![Build Status](https://travis-ci.com/aseigneurin/kafka-tutorial-kafka-h2o.svg?branch=master)](https://travis-ci.com/aseigneurin/kafka-tutorial-kafka-h2o)

See [Realtime Machine Learning predictions with Kafka and H2O.ai](https://aseigneurin.github.io/2018/09/05/realtime-machine-learning-predictions-wth-kafka-and-h2o.html).

## Data

File: `data/housedata.csv`

Data types:
- `date`: time
- `price`: numeric
- `bedrooms`: numeric
- `bathrooms`: numeric
- `sqft_living`: numeric
- `sqft_lot`: numeric
- `floors`: numeric
- `waterfront`: enum
- `view`: enum
- `condition`: enum
- `sqft_above`: numeric
- `sqft_basement`: numeric
- `yr_built`: numeric
- `yr_renovated`: numeric
- `street`: string
- `city`: enum
- `statezip`: enum
- `country`: enum

Splits:
- 70% for training
- 20% for validation
- 10% for test

## Model

- Type: Gradient Boosting Machine
- Training frame: 70%
- Validation frame: 20%
- Response column: `price`
- Ignored columns: `date`
- `ntrees`: 120

## Kafka

```
$ kafka-topics --zookeeper localhost:2181 --create --topic housing --replication-factor 1 --partitions 4
$ kafka-topics --zookeeper localhost:2181 --create --topic predictions --replication-factor 1 --partitions 4
$ kafka-topics --zookeeper localhost:2181 --create --topic zipcodes --replication-factor 1 --partitions 4
```
