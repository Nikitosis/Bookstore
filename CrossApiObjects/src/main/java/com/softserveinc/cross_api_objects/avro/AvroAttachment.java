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
public class AvroAttachment extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -2707574428428272039L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AvroAttachment\",\"namespace\":\"com.softserveinc.cross_api_objects.avro\",\"fields\":[{\"name\":\"attachmentUrl\",\"type\":\"string\"},{\"name\":\"attachmentName\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<AvroAttachment> ENCODER =
      new BinaryMessageEncoder<AvroAttachment>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<AvroAttachment> DECODER =
      new BinaryMessageDecoder<AvroAttachment>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<AvroAttachment> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<AvroAttachment> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<AvroAttachment>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this AvroAttachment to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a AvroAttachment from a ByteBuffer. */
  public static AvroAttachment fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.lang.CharSequence attachmentUrl;
  @Deprecated public java.lang.CharSequence attachmentName;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public AvroAttachment() {}

  /**
   * All-args constructor.
   * @param attachmentUrl The new value for attachmentUrl
   * @param attachmentName The new value for attachmentName
   */
  public AvroAttachment(java.lang.CharSequence attachmentUrl, java.lang.CharSequence attachmentName) {
    this.attachmentUrl = attachmentUrl;
    this.attachmentName = attachmentName;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return attachmentUrl;
    case 1: return attachmentName;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: attachmentUrl = (java.lang.CharSequence)value$; break;
    case 1: attachmentName = (java.lang.CharSequence)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'attachmentUrl' field.
   * @return The value of the 'attachmentUrl' field.
   */
  public java.lang.CharSequence getAttachmentUrl() {
    return attachmentUrl;
  }

  /**
   * Sets the value of the 'attachmentUrl' field.
   * @param value the value to set.
   */
  public void setAttachmentUrl(java.lang.CharSequence value) {
    this.attachmentUrl = value;
  }

  /**
   * Gets the value of the 'attachmentName' field.
   * @return The value of the 'attachmentName' field.
   */
  public java.lang.CharSequence getAttachmentName() {
    return attachmentName;
  }

  /**
   * Sets the value of the 'attachmentName' field.
   * @param value the value to set.
   */
  public void setAttachmentName(java.lang.CharSequence value) {
    this.attachmentName = value;
  }

  /**
   * Creates a new AvroAttachment RecordBuilder.
   * @return A new AvroAttachment RecordBuilder
   */
  public static com.softserveinc.cross_api_objects.avro.AvroAttachment.Builder newBuilder() {
    return new com.softserveinc.cross_api_objects.avro.AvroAttachment.Builder();
  }

  /**
   * Creates a new AvroAttachment RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new AvroAttachment RecordBuilder
   */
  public static com.softserveinc.cross_api_objects.avro.AvroAttachment.Builder newBuilder(com.softserveinc.cross_api_objects.avro.AvroAttachment.Builder other) {
    return new com.softserveinc.cross_api_objects.avro.AvroAttachment.Builder(other);
  }

  /**
   * Creates a new AvroAttachment RecordBuilder by copying an existing AvroAttachment instance.
   * @param other The existing instance to copy.
   * @return A new AvroAttachment RecordBuilder
   */
  public static com.softserveinc.cross_api_objects.avro.AvroAttachment.Builder newBuilder(com.softserveinc.cross_api_objects.avro.AvroAttachment other) {
    return new com.softserveinc.cross_api_objects.avro.AvroAttachment.Builder(other);
  }

  /**
   * RecordBuilder for AvroAttachment instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AvroAttachment>
    implements org.apache.avro.data.RecordBuilder<AvroAttachment> {

    private java.lang.CharSequence attachmentUrl;
    private java.lang.CharSequence attachmentName;

    /** Creates a new Builder */
    Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.softserveinc.cross_api_objects.avro.AvroAttachment.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.attachmentUrl)) {
        this.attachmentUrl = data().deepCopy(fields()[0].schema(), other.attachmentUrl);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.attachmentName)) {
        this.attachmentName = data().deepCopy(fields()[1].schema(), other.attachmentName);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing AvroAttachment instance
     * @param other The existing instance to copy.
     */
    private Builder(com.softserveinc.cross_api_objects.avro.AvroAttachment other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.attachmentUrl)) {
        this.attachmentUrl = data().deepCopy(fields()[0].schema(), other.attachmentUrl);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.attachmentName)) {
        this.attachmentName = data().deepCopy(fields()[1].schema(), other.attachmentName);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'attachmentUrl' field.
      * @return The value.
      */
    public java.lang.CharSequence getAttachmentUrl() {
      return attachmentUrl;
    }

    /**
      * Sets the value of the 'attachmentUrl' field.
      * @param value The value of 'attachmentUrl'.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroAttachment.Builder setAttachmentUrl(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.attachmentUrl = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'attachmentUrl' field has been set.
      * @return True if the 'attachmentUrl' field has been set, false otherwise.
      */
    public boolean hasAttachmentUrl() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'attachmentUrl' field.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroAttachment.Builder clearAttachmentUrl() {
      attachmentUrl = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'attachmentName' field.
      * @return The value.
      */
    public java.lang.CharSequence getAttachmentName() {
      return attachmentName;
    }

    /**
      * Sets the value of the 'attachmentName' field.
      * @param value The value of 'attachmentName'.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroAttachment.Builder setAttachmentName(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.attachmentName = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'attachmentName' field has been set.
      * @return True if the 'attachmentName' field has been set, false otherwise.
      */
    public boolean hasAttachmentName() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'attachmentName' field.
      * @return This builder.
      */
    public com.softserveinc.cross_api_objects.avro.AvroAttachment.Builder clearAttachmentName() {
      attachmentName = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AvroAttachment build() {
      try {
        AvroAttachment record = new AvroAttachment();
        record.attachmentUrl = fieldSetFlags()[0] ? this.attachmentUrl : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.attachmentName = fieldSetFlags()[1] ? this.attachmentName : (java.lang.CharSequence) defaultValue(fields()[1]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<AvroAttachment>
    WRITER$ = (org.apache.avro.io.DatumWriter<AvroAttachment>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<AvroAttachment>
    READER$ = (org.apache.avro.io.DatumReader<AvroAttachment>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
