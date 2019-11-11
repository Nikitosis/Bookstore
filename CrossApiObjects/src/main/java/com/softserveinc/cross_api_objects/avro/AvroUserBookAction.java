/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.softserveinc.cross_api_objects.avro;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class AvroUserBookAction extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -4940216278985009940L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AvroUserBookAction\",\"namespace\":\"com.softserveinc.cross_api_objects.avro\",\"fields\":[{\"name\":\"userId\",\"type\":\"long\"},{\"name\":\"bookId\",\"type\":\"long\"},{\"name\":\"date\",\"type\":\"long\"},{\"name\":\"action\",\"type\":{\"type\":\"enum\",\"name\":\"AvroUserBookActionType\",\"symbols\":[\"TAKE\",\"RETURN\"]}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<AvroUserBookAction> ENCODER =
      new BinaryMessageEncoder<AvroUserBookAction>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<AvroUserBookAction> DECODER =
      new BinaryMessageDecoder<AvroUserBookAction>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<AvroUserBookAction> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<AvroUserBookAction> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<AvroUserBookAction>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this AvroUserBookAction to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a AvroUserBookAction from a ByteBuffer. */
  public static AvroUserBookAction fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public long userId;
  @Deprecated public long bookId;
  @Deprecated public long date;
  @Deprecated public com.softserveinc.cross_api_objects.avro.AvroUserBookActionType action;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public AvroUserBookAction() {}

  /**
   * All-args constructor.
   * @param userId The new value for userId
   * @param bookId The new value for bookId
   * @param date The new value for date
   * @param action The new value for action
   */
  public AvroUserBookAction(java.lang.Long userId, java.lang.Long bookId, java.lang.Long date, com.softserveinc.cross_api_objects.avro.AvroUserBookActionType action) {
    this.userId = userId;
    this.bookId = bookId;
    this.date = date;
    this.action = action;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return userId;
    case 1: return bookId;
    case 2: return date;
    case 3: return action;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: userId = (java.lang.Long)value$; break;
    case 1: bookId = (java.lang.Long)value$; break;
    case 2: date = (java.lang.Long)value$; break;
    case 3: action = (com.softserveinc.cross_api_objects.avro.AvroUserBookActionType)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'userId' field.
   * @return The value of the 'userId' field.
   */
  public java.lang.Long getUserId() {
    return userId;
  }

  /**
   * Sets the value of the 'userId' field.
   * @param value the value to set.
   */
  public void setUserId(java.lang.Long value) {
    this.userId = value;
  }

  /**
   * Gets the value of the 'bookId' field.
   * @return The value of the 'bookId' field.
   */
  public java.lang.Long getBookId() {
    return bookId;
  }

  /**
   * Sets the value of the 'bookId' field.
   * @param value the value to set.
   */
  public void setBookId(java.lang.Long value) {
    this.bookId = value;
  }

  /**
   * Gets the value of the 'date' field.
   * @return The value of the 'date' field.
   */
  public java.lang.Long getDate() {
    return date;
  }

  /**
   * Sets the value of the 'date' field.
   * @param value the value to set.
   */
  public void setDate(java.lang.Long value) {
    this.date = value;
  }

  /**
   * Gets the value of the 'action' field.
   * @return The value of the 'action' field.
   */
  public com.softserveinc.cross_api_objects.avro.AvroUserBookActionType getAction() {
    return action;
  }

  /**
   * Sets the value of the 'action' field.
   * @param value the value to set.
   */
  public void setAction(com.softserveinc.cross_api_objects.avro.AvroUserBookActionType value) {
    this.action = value;
  }

  /**
   * Creates a new AvroUserBookAction RecordBuilder.
   * @return A new AvroUserBookAction RecordBuilder
   */
  public static com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder newBuilder() {
    return new com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder();
  }

  /**
   * Creates a new AvroUserBookAction RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new AvroUserBookAction RecordBuilder
   */
  public static com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder newBuilder(com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder other) {
    return new com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder(other);
  }

  /**
   * Creates a new AvroUserBookAction RecordBuilder by copying an existing AvroUserBookAction instance.
   * @param other The existing instance to copy.
   * @return A new AvroUserBookAction RecordBuilder
   */
  public static com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder newBuilder(com.softserveinc.cross_api_objects.avro.AvroUserBookAction other) {
    return new com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder(other);
  }

  /**
   * RecordBuilder for AvroUserBookAction instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AvroUserBookAction>
    implements org.apache.avro.data.RecordBuilder<AvroUserBookAction> {

    private long userId;
    private long bookId;
    private long date;
    private com.softserveinc.cross_api_objects.avro.AvroUserBookActionType action;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.userId)) {
        this.userId = data().deepCopy(fields()[0].schema(), other.userId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.bookId)) {
        this.bookId = data().deepCopy(fields()[1].schema(), other.bookId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.date)) {
        this.date = data().deepCopy(fields()[2].schema(), other.date);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.action)) {
        this.action = data().deepCopy(fields()[3].schema(), other.action);
        fieldSetFlags()[3] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing AvroUserBookAction instance
     * @param other The existing instance to copy.
     */
    private Builder(com.softserveinc.cross_api_objects.avro.AvroUserBookAction other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.userId)) {
        this.userId = data().deepCopy(fields()[0].schema(), other.userId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.bookId)) {
        this.bookId = data().deepCopy(fields()[1].schema(), other.bookId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.date)) {
        this.date = data().deepCopy(fields()[2].schema(), other.date);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.action)) {
        this.action = data().deepCopy(fields()[3].schema(), other.action);
        fieldSetFlags()[3] = true;
      }
    }

    /**
      * Gets the value of the 'userId' field.
      * @return The value.
      */
    public java.lang.Long getUserId() {
      return userId;
    }

    /**
      * Sets the value of the 'userId' field.
      * @param value The value of 'userId'.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder setUserId(long value) {
      validate(fields()[0], value);
      this.userId = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'userId' field has been set.
      * @return True if the 'userId' field has been set, false otherwise.
      */
    public boolean hasUserId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'userId' field.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder clearUserId() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'bookId' field.
      * @return The value.
      */
    public java.lang.Long getBookId() {
      return bookId;
    }

    /**
      * Sets the value of the 'bookId' field.
      * @param value The value of 'bookId'.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder setBookId(long value) {
      validate(fields()[1], value);
      this.bookId = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'bookId' field has been set.
      * @return True if the 'bookId' field has been set, false otherwise.
      */
    public boolean hasBookId() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'bookId' field.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder clearBookId() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'date' field.
      * @return The value.
      */
    public java.lang.Long getDate() {
      return date;
    }

    /**
      * Sets the value of the 'date' field.
      * @param value The value of 'date'.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder setDate(long value) {
      validate(fields()[2], value);
      this.date = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'date' field has been set.
      * @return True if the 'date' field has been set, false otherwise.
      */
    public boolean hasDate() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'date' field.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder clearDate() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'action' field.
      * @return The value.
      */
    public com.softserveinc.cross_api_objects.avro.AvroUserBookActionType getAction() {
      return action;
    }

    /**
      * Sets the value of the 'action' field.
      * @param value The value of 'action'.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder setAction(com.softserveinc.cross_api_objects.avro.AvroUserBookActionType value) {
      validate(fields()[3], value);
      this.action = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'action' field has been set.
      * @return True if the 'action' field has been set, false otherwise.
      */
    public boolean hasAction() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'action' field.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroUserBookAction.Builder clearAction() {
      action = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AvroUserBookAction build() {
      try {
        AvroUserBookAction record = new AvroUserBookAction();
        record.userId = fieldSetFlags()[0] ? this.userId : (java.lang.Long) defaultValue(fields()[0]);
        record.bookId = fieldSetFlags()[1] ? this.bookId : (java.lang.Long) defaultValue(fields()[1]);
        record.date = fieldSetFlags()[2] ? this.date : (java.lang.Long) defaultValue(fields()[2]);
        record.action = fieldSetFlags()[3] ? this.action : (com.softserveinc.cross_api_objects.avro.AvroUserBookActionType) defaultValue(fields()[3]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<AvroUserBookAction>
    WRITER$ = (org.apache.avro.io.DatumWriter<AvroUserBookAction>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<AvroUserBookAction>
    READER$ = (org.apache.avro.io.DatumReader<AvroUserBookAction>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
