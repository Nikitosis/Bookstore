package com.crossapi.testutils.mixins;

import com.crossapi.models.Book;
import com.crossapi.models.User;
import com.crossapi.models.UserMixin;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class MixinModule extends Module {

    public MixinModule() {
        super();
    }

    @Override
    public Object getTypeId() {
        return super.getTypeId();
    }

    @Override
    public String getModuleName() {
        return "MixinModule";
    }

    @Override
    public Version version() {
        return new Version(1,0,0,"");
    }

    @Override
    public void setupModule(SetupContext setupContext) {
        setupContext.setMixInAnnotations(Book.class,BookMixin.class);
        setupContext.setMixInAnnotations(User.class, UserMixin.class);
    }
}
