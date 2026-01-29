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
package fr.paris.lutece.plugins.rest.modules.healthcheck.business.impl;

import java.lang.management.ManagementFactory;

import java.lang.management.MemoryMXBean;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
public class MemoryJVMUsageCheck implements HealthCheck
{
	private static double MEMORY_MAX = 0.9;
	
    @Override
    public HealthCheckResponse call( )
    {
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean( );
        
       
        //Memory heap
        
        long memHeapUsed = memBean.getHeapMemoryUsage( ).getUsed( ); //Memory heap used by JVM
        long memHeapMax = memBean.getHeapMemoryUsage( ).getMax( );   //Total memory available for JVM heap memory
        boolean isHeapHealthy = memHeapUsed < memHeapMax * 0.9; //Test if memory used is lower than 90% of total memory 
        
        //Memory no heap
        long memNoHeapUsed = memBean.getNonHeapMemoryUsage().getUsed(); //Memory No heap used by JVM
        long memNoHeapMax = memBean.getNonHeapMemoryUsage().getMax(); ////Total memory available for JVM No heap memory
        
        boolean isNoHeapHealthy = true;
        // -1 means no limit is set for no heap memory
        if(memNoHeapMax!=-1) {
        	isNoHeapHealthy=memNoHeapUsed < memNoHeapMax * MEMORY_MAX; //Check if no heap memory is not used at more than 90% of its capacity
        }
        
        boolean isHealthy =  isHeapHealthy & isNoHeapHealthy;
        
        StringBuilder sb = new StringBuilder( );
        
        return HealthCheckResponse.named( "Memory JVM Check" ).status( isHealthy )
                .withData( "message", sb.append("Memory available (b) : ").append( (memHeapMax-memHeapUsed) ).append( " / " ).append( memHeapMax ).toString( ) )
                .build( );
    }

}
