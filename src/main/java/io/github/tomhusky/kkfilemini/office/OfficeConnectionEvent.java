package io.github.tomhusky.kkfilemini.office;

import java.util.EventObject;

class OfficeConnectionEvent extends EventObject {

    private static final long serialVersionUID = 2060652797570876077L;

    public OfficeConnectionEvent(OfficeConnection source) {
        super(source);
    }

}
