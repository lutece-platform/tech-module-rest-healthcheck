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
package fr.paris.lutece.plugins.rest.modules.healthcheck.rs;

import java.util.Collections;
import java.util.Map;

import org.eclipse.microprofile.health.HealthCheckResponse;

/**
 * JSON form of a health check response: name, status and data.
 */
public class HealthCheckResult
{
    private final String _strName;
    private final String _strStatus;
    private final Map<String, Object> _mapData;

    /**
     * Builds the JSON form of a health check response.
     *
     * @param response
     *            the health check response
     */
    public HealthCheckResult( HealthCheckResponse response )
    {
        _strName = response.getName( );
        _strStatus = response.getStatus( ).name( );
        _mapData = response.getData( ).orElse( Collections.emptyMap( ) );
    }

    /**
     * Returns the name of the check.
     *
     * @return the name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Returns the status of the check, UP or DOWN.
     *
     * @return the status
     */
    public String getStatus( )
    {
        return _strStatus;
    }

    /**
     * Returns the data of the check.
     *
     * @return the data, empty when the check carries none
     */
    public Map<String, Object> getData( )
    {
        return _mapData;
    }
}
