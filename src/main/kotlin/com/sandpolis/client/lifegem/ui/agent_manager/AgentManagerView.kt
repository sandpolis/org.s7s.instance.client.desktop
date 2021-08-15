//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//

package com.sandpolis.client.lifegem.ui.agent_manager

import com.sandpolis.client.lifegem.api.AgentView
import com.sandpolis.client.lifegem.ui.common.pane.CarouselPane
import com.sandpolis.core.foundation.Platform
import com.sandpolis.core.instance.state.InstanceOids.ProfileOid.AgentOid
import com.sandpolis.core.instance.state.st.STDocument
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.TreeItem
import javafx.scene.layout.Region
import tornadofx.*

class AgentManagerView : Fragment() {

    val profile: STDocument by param()

    private val model = object : ViewModel() {
        val extendBottom = bind { SimpleObjectProperty<Region>() }
    }

    val views = listOf(InventoryView(), BootagentView(profile))

    val carousel = CarouselPane().apply {
        directionProperty().set(Side.TOP)

        views.forEach {
            add(it.name, it.root)
        }
    }

    override val root = borderpane {
        prefWidth = 800.0
        prefHeight = 400.0

        left = titledpane(profile.attribute(AgentOid.HOSTNAME).asString()) {
            alignment = Pos.CENTER
            isCollapsible = false
            prefWidth = 150.0
            style(append = true) {
                padding = box(0.px, 5.px, 5.px, 5.px)
            }
            vbox {
                when (profile.attribute(AgentOid.OS_TYPE).asOsType()) {
                    Platform.OsType.LINUX -> hbox {
                        imageview("image/platform/linux.png")
                        label("Linux")
                    }
                    Platform.OsType.WINDOWS -> hbox {
                        imageview("image/platform/windows_10.png")
                        label("Windows")
                    }
                    Platform.OsType.DARWIN -> hbox {
                        imageview("image/platform/osx.png")
                        label("macOS")
                    }
                    else -> hbox {
                        label("Unknown")
                    }
                }
                hbox {
                    imageview("image/flag/US.png")
                    vbox {
                        label("127.0.0.1")
                        label("United States")
                    }
                }
                hbox {
                    imageview()
                    label("54 days")
                }
            }
            flowpane {
                hgap = 10.0
                vgap = 10.0
                alignment = Pos.CENTER

                button("P") {
                    tooltip("Power controls")
                }
                button("C") {
                    tooltip("Connection controls")
                }
                button("T") {
                    tooltip("")
                }
            }
            treeview<AgentView> {
                isShowRoot = false

                root = TreeItem(object : AgentView("Root") {
                    override val root = pane {}
                    override fun setActive(profile: STDocument) {}
                    override fun setInactive() {}
                })

                cellFormat {
                    text = it.name
                }

                populate { parent ->
                    if (parent == root) {
                        views
                    } else {
                        null
                    }
                }

                onUserSelect {
                    views.forEach(AgentView::setInactive)
                    it.setActive(profile)
                    carousel.moveTo(it.name)
                }
            }
        }
        center = carousel
    }
}
