package com.twitter.heron.resource;


import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Ignore;

import com.twitter.heron.api.spout.IRichSpout;
import com.twitter.heron.api.spout.SpoutOutputCollector;
import com.twitter.heron.api.topology.OutputFieldsDeclarer;
import com.twitter.heron.api.topology.TopologyContext;
import com.twitter.heron.api.tuple.Fields;
import com.twitter.heron.api.tuple.Values;
import com.twitter.heron.common.core.base.SingletonRegistry;

/**
 * A Spout used for unit test, it will:
 * 1. It will emit EMIT_COUNT of tuples with MESSAGE_ID.
 * 2. When it receives an ack, it will increment the singleton Constants.ACK_COUNT
 * 3. When it receives a fail, it will increment the singleton Constants.FAIL_COUNT
 * 4. The tuples are declared by outputFieldsDeclarer in fields "word"
 */

@Ignore
public class TestSpout implements IRichSpout {
  static final int EMIT_COUNT = 10;
  SpoutOutputCollector outputCollector;

  private String MESSAGE_ID = "MESSAGE_ID";
  private int emitted = 0;
  private final String[] toSend = new String[]{"A", "B"};

  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    outputFieldsDeclarer.declare(new Fields("word"));
  }

  @Override
  public Map<String, Object> getComponentConfiguration() {
    return null;
  }

  @Override
  public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
    this.outputCollector = spoutOutputCollector;
  }

  @Override
  public void close() {

  }

  @Override
  public void activate() {
    AtomicInteger activateCount =
        (AtomicInteger) SingletonRegistry.INSTANCE.getSingleton(Constants.ACTIVATE_COUNT);
    if (activateCount != null) {
      activateCount.getAndIncrement();
    }
  }

  @Override
  public void deactivate() {
    AtomicInteger deactivateCount =
        (AtomicInteger) SingletonRegistry.INSTANCE.getSingleton(Constants.DEACTIVATE_COUNT);
    if (deactivateCount != null) {
      deactivateCount.getAndIncrement();
    }
  }

  @Override
  public void nextTuple() {
    // It will emit A, B, A, B, A, B, A, B, A, B
    if (emitted < EMIT_COUNT) {
      String word = toSend[emitted % toSend.length];
      outputCollector.emit(new Values(word), MESSAGE_ID);
      emitted++;
    }
  }

  @Override
  public void ack(Object o) {
    AtomicInteger ackCount = (AtomicInteger) SingletonRegistry.INSTANCE.getSingleton(Constants.ACK_COUNT);
    if (ackCount != null) {
      ackCount.getAndIncrement();
    }
  }

  @Override
  public void fail(Object o) {
    AtomicInteger failCount = (AtomicInteger) SingletonRegistry.INSTANCE.getSingleton(Constants.FAIL_COUNT);
    if (failCount != null) {
      failCount.getAndIncrement();
    }


  }
}