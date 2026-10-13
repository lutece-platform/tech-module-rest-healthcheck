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
package fr.paris.lutece.plugins.rest.modules.healthcheck.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.rest.modules.healthcheck.business.LuteceHealthCheckResponseProvider;
import fr.paris.lutece.plugins.rest.modules.healthcheck.rs.HealthCheckResult;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;

/**
 * Tests the selection of the health checks by qualifier.
 */
public class HealthCheckServiceTest extends LuteceTestCase
{
    private static final String PLUGIN_NAME = "rest-healthcheck";

    @Inject
    private HealthCheckService _healthCheckService;

    /**
     * Installs a response provider, the server one being absent from the tests.
     */
    @BeforeAll
    static void setUpProvider( )
    {
        HealthCheckResponse.setResponseProvider( new LuteceHealthCheckResponseProvider( ) );
    }

    /**
     * Enables the module, whose checks run only while it is enabled.
     */
    @BeforeEach
    public void enableModule( )
    {
        PluginService.getPlugin( PLUGIN_NAME ).install( );
    }

    /**
     * Readiness runs the database and forced status checks.
     */
    @Test
    public void testReadiness( )
    {
        assertEquals( Set.of( "DB.Healthcheck", "Forced.Healthcheck" ), names( _healthCheckService.check( Readiness.Literal.INSTANCE ) ) );
    }

    /**
     * Liveness runs the memory and CPU checks, both up.
     */
    @Test
    public void testLiveness( )
    {
        List<HealthCheckResponse> list = _healthCheckService.check( Liveness.Literal.INSTANCE );

        assertEquals( Set.of( "System Liveness Check", "CPU usage Check" ), names( list ) );
        list.forEach( r -> assertEquals( HealthCheckResponse.Status.UP, r.getStatus( ), r.getName( ) ) );
    }

    /**
     * No qualifier runs every check, and the forced status is up by default.
     */
    @Test
    public void testAll( )
    {
        List<HealthCheckResponse> list = _healthCheckService.check( null );

        assertEquals( Set.of( "DB.Healthcheck", "Forced.Healthcheck", "System Liveness Check", "CPU usage Check" ), names( list ) );
        assertEquals( HealthCheckResponse.Status.UP,
                list.stream( ).filter( r -> "Forced.Healthcheck".equals( r.getName( ) ) ).findFirst( ).orElseThrow( ).getStatus( ) );
    }

    /**
     * No check runs while the module is disabled.
     */
    @Test
    public void testDisabledModule( )
    {
        PluginService.getPlugin( PLUGIN_NAME ).uninstall( );

        assertTrue( _healthCheckService.check( null ).isEmpty( ) );
    }

    /**
     * A response is written as name, data and status.
     */
    @Test
    public void testJson( )
    {
        List<HealthCheckResult> list = _healthCheckService.check( Liveness.Literal.INSTANCE ).stream( ).map( HealthCheckResult::new ).toList( );
        String strJson = JsonUtil.buildJsonResponse( new JsonResponse( list ) );

        assertTrue( strJson.contains( "{\"name\":\"System Liveness Check\",\"data\":{\"message\":\"Memory usage : " ), strJson );
        assertTrue( strJson.contains( "\"status\":\"UP\"}" ), strJson );
    }

    /**
     * Collects the names of the responses.
     *
     * @param list
     *            the responses
     * @return the names
     */
    private static Set<String> names( List<HealthCheckResponse> list )
    {
        return list.stream( ).map( HealthCheckResponse::getName ).collect( Collectors.toSet( ) );
    }
}
