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
public class AvroInvoiceAction extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -1813108494383545800L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AvroInvoiceAction\",\"namespace\":\"com.softserveinc.cross_api_objects.avro\",\"fields\":[{\"name\":\"userId\",\"type\":\"long\"},{\"name\":\"invoiceUrl\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<AvroInvoiceAction> ENCODER =
      new BinaryMessageEncoder<AvroInvoiceAction>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<AvroInvoiceAction> DECODER =
      new BinaryMessageDecoder<AvroInvoiceAction>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<AvroInvoiceAction> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<AvroInvoiceAction> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<AvroInvoiceAction>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this AvroInvoiceAction to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a AvroInvoiceAction from a ByteBuffer. */
  public static AvroInvoiceAction fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public long userId;
  @Deprecated public java.lang.CharSequence invoiceUrl;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public AvroInvoiceAction() {}

  /**
   * All-args constructor.
   * @param userId The new value for userId
   * @param invoiceUrl The new value for invoiceUrl
   */
  public AvroInvoiceAction(java.lang.Long userId, java.lang.CharSequence invoiceUrl) {
    this.userId = userId;
    this.invoiceUrl = invoiceUrl;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return userId;
    case 1: return invoiceUrl;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: userId = (java.lang.Long)value$; break;
    case 1: invoiceUrl = (java.lang.CharSequence)value$; break;
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
   * Gets the value of the 'invoiceUrl' field.
   * @return The value of the 'invoiceUrl' field.
   */
  public java.lang.CharSequence getInvoiceUrl() {
    return invoiceUrl;
  }

  /**
   * Sets the value of the 'invoiceUrl' field.
   * @param value the value to set.
   */
  public void setInvoiceUrl(java.lang.CharSequence value) {
    this.invoiceUrl = value;
  }

  /**
   * Creates a new AvroInvoiceAction RecordBuilder.
   * @return A new AvroInvoiceAction RecordBuilder
   */
  public static com.softserveinc.cross_api_objects.avro.AvroInvoiceAction.Builder newBuilder() {
    return new com.softserveinc.cross_api_objects.avro.AvroInvoiceAction.Builder();
  }

  /**
   * Creates a new AvroInvoiceAction RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new AvroInvoiceAction RecordBuilder
   */
  public static com.softserveinc.cross_api_objects.avro.AvroInvoiceAction.Builder newBuilder(com.softserveinc.cross_api_objects.avro.AvroInvoiceAction.Builder other) {
    return new com.softserveinc.cross_api_objects.avro.AvroInvoiceAction.Builder(other);
  }

  /**
   * Creates a new AvroInvoiceAction RecordBuilder by copying an existing AvroInvoiceAction instance.
   * @param other The existing instance to copy.
   * @return A new AvroInvoiceAction RecordBuilder
   */
  public static com.softserveinc.cross_api_objects.avro.AvroInvoiceAction.Builder newBuilder(com.softserveinc.cross_api_objects.avro.AvroInvoiceAction other) {
    return new com.softserveinc.cross_api_objects.avro.AvroInvoiceAction.Builder(other);
  }

  /**
   * RecordBuilder for AvroInvoiceAction instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AvroInvoiceAction>
    implements org.apache.avro.data.RecordBuilder<AvroInvoiceAction> {

    private long userId;
    private java.lang.CharSequence invoiceUrl;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.softserveinc.cross_api_objects.avro.AvroInvoiceAction.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.userId)) {
        this.userId = data().deepCopy(fields()[0].schema(), other.userId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.invoiceUrl)) {
        this.invoiceUrl = data().deepCopy(fields()[1].schema(), other.invoiceUrl);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing AvroInvoiceAction instance
     * @param other The existing instance to copy.
     */
    private Builder(com.softserveinc.cross_api_objects.avro.AvroInvoiceAction other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.userId)) {
        this.userId = data().deepCopy(fields()[0].schema(), other.userId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.invoiceUrl)) {
        this.invoiceUrl = data().deepCopy(fields()[1].schema(), other.invoiceUrl);
        fieldSetFlags()[1] = true;
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
    public com.softserveinc.cross_api_objects.avro.AvroInvoiceAction.Builder setUserId(long value) {
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
    public com.softserveinc.cross_api_objects.avro.AvroInvoiceAction.Builder clearUserId() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'invoiceUrl' field.
      * @return The value.
      */
    public java.lang.CharSequence getInvoiceUrl() {
      return invoiceUrl;
    }

    /**
      * Sets the value of the 'invoiceUrl' field.
      * @param value The value of 'invoiceUrl'.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroInvoiceAction.Builder setInvoiceUrl(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.invoiceUrl = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'invoiceUrl' field has been set.
      * @return True if the 'invoiceUrl' field has been set, false otherwise.
      */
    public boolean hasInvoiceUrl() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'invoiceUrl' field.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroInvoiceAction.Builder clearInvoiceUrl() {
      invoiceUrl = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AvroInvoiceAction build() {
      try {
        AvroInvoiceAction record = new AvroInvoiceAction();
        record.userId = fieldSetFlags()[0] ? this.userId : (java.lang.Long) defaultValue(fields()[0]);
        record.invoiceUrl = fieldSetFlags()[1] ? this.invoiceUrl : (java.lang.CharSequence) defaultValue(fields()[1]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<AvroInvoiceAction>
    WRITER$ = (org.apache.avro.io.DatumWriter<AvroInvoiceAction>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<AvroInvoiceAction>
    READER$ = (org.apache.avro.io.DatumReader<AvroInvoiceAction>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
