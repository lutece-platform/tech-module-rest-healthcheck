/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.rest.modules.healthcheck.business;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import fr.paris.lutece.portal.service.util.AppLogService;

class LuteceHealthCheckResponseBuilder extends HealthCheckResponseBuilder
{

    private String name;

    private HealthCheckResponse.Status status = HealthCheckResponse.Status.DOWN;

    private final Map<String, Object> data = new LinkedHashMap<>( );

    @Override
    public HealthCheckResponseBuilder name( String name )
    {
        this.name = name;
        return this;
    }

    @Override
    public HealthCheckResponseBuilder withData( String key, String value )
    {
        this.data.put( key, value );
        return this;
    }

    @Override
    public HealthCheckResponseBuilder withData( String key, long value )
    {
        this.data.put( key, value );
        return this;
    }

    @Override
    public HealthCheckResponseBuilder withData( String key, boolean value )
    {
        this.data.put( key, value );
        return this;
    }

    @Override
    public HealthCheckResponseBuilder up( )
    {
        this.status = HealthCheckResponse.Status.UP;
        return this;
    }

    @Override
    public HealthCheckResponseBuilder down( )
    {
        this.status = HealthCheckResponse.Status.DOWN;
        return this;
    }

    @Override
    public HealthCheckResponseBuilder status( boolean up )
    {
        if ( up )
        {
            return up( );
        }

        return down( );
    }

    @Override
    public HealthCheckResponse build( )
    {
        if ( null == this.name || this.name.trim( ).length( ) == 0 )
        {
            AppLogService.error( "Healthcheck must be named" );
        }

        return new HealthCheckResponse( this.name, this.status, Optional.of( this.data ) );
    }

}
