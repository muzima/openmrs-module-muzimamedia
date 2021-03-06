package org.openmrs.module.muzimamedia.resources;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzimamedia.MuzimaMediaTag;
import org.openmrs.module.muzimamedia.api.MuzimaMediaTagService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MuzimaMediaTagResourceTest  {
    private MuzimaMediaTagService service;
    MuzimaMediaTagResource controller;

    @Before
    public void setUp() throws Exception {
        ArrayList<MuzimaMediaTag> tags = getTags("foo", "bar", "baz");
        service = mock(MuzimaMediaTagService.class);
        when(service.getAll()).thenReturn(tags);
        controller = new MuzimaMediaTagResource();
        mockStatic(Context.class);
        PowerMockito.when(Context.getService(MuzimaMediaTagService.class)).thenReturn(service);
    }

    private ArrayList<MuzimaMediaTag> getTags(String... tags) {
        ArrayList<MuzimaMediaTag> muzimaFormTags = new ArrayList<MuzimaMediaTag>();
        for (String tag : tags) {
            muzimaFormTags.add(new MuzimaMediaTag(tag));
        }
        return muzimaFormTags;
    }


    @Test
    public void getAll_shouldGetAllTags() {
        Representation representation = mock(CustomRepresentation.class);
        RequestContext context = mock(RequestContext.class);

        RestService restService = mock(RestService.class);
        when(restService.getResourceBySupportedClass(MuzimaMediaTagResource.class)).thenReturn(null);
        PowerMockito.when(Context.getService(RestService.class)).thenReturn(restService);


        when(representation.getRepresentation()).thenReturn("(uuid:uuid,id:id)");
        when(context.getRepresentation()).thenReturn(representation);
        when(context.getStartIndex()).thenReturn(0);
        when(context.getLimit()).thenReturn(10);

        SimpleObject response = controller.getAll(context);
        assertThat(response.containsKey("results"), is(true));
        List forms = (List) response.get("results");
        assertThat(forms.size(), is(3));
        verify(service, times(1)).getAll();
    }

    @Test
    public void getRepresentationDescription_shouldAddDefaultProperties() {
        Representation representation = mock(RefRepresentation.class);
        Set<String> keys = controller.getRepresentationDescription(representation).getProperties().keySet();
        assertThat(keys.contains("id"), is(true));
        assertThat(keys.contains("uuid"), is(true));
        assertThat(keys.contains("name"), is(true));
    }
}